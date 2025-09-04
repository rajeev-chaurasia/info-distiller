import pika
import json
import time
import os
from .daily_job import run_job_for_user

def main():
    rabbitmq_host = os.getenv('RABBITMQ_HOST', 'rabbitmq')
    rabbitmq_user = os.getenv('RABBITMQ_USER', 'user')
    rabbitmq_pass = os.getenv('RABBITMQ_PASS', 'password')

    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_pass)

    connection = None
    while not connection:
        try:
            print("Worker: Attempting to connect to RabbitMQ...")
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=rabbitmq_host, credentials=credentials)
            )
            print("Worker: Connection to RabbitMQ successful.")
        except pika.exceptions.AMQPConnectionError as e:
            print(f"Worker: RabbitMQ not ready yet. Error: {e}. Retrying in 5 seconds...")
            time.sleep(5)

    channel = connection.channel()
    channel.queue_declare(queue='briefing_jobs', durable=True)

    def callback(ch, method, properties, body):
        print(f" [x] Worker received job: {body.decode()}")
        try:
            data = json.loads(body)
            user_id = data.get('userId')
            if user_id:
                run_job_for_user(user_id)
            else:
                print(" [!] Error: No userId in message")

            ch.basic_ack(delivery_tag=method.delivery_tag)
            print(f" [x] Worker finished job for user {user_id}")
        except Exception as e:
            print(f" [!] Error processing job: {e}")
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='briefing_jobs', on_message_callback=callback)

    print(' [*] Worker is now waiting for messages.')
    channel.start_consuming()

if __name__ == '__main__':
    main()