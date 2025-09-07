import pika
import json
import time
import os
import threading
from .daily_job import run_job_for_user


def main():
    rabbitmq_host = os.getenv('RABBITMQ_HOST', 'rabbitmq')
    rabbitmq_user = os.getenv('RABBITMQ_USER', 'user')
    rabbitmq_pass = os.getenv('RABBITMQ_PASS', 'password')
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_pass)

    connection = None
    while not connection:
        try:
            print("Worker: Connecting to RabbitMQ...")
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=rabbitmq_host, credentials=credentials, heartbeat=600)
            )
            print("Worker: Connected to RabbitMQ.")
        except pika.exceptions.AMQPConnectionError:
            print("Worker: RabbitMQ not ready. Retrying in 5 seconds...")
            time.sleep(5)

    channel = connection.channel()
    channel.queue_declare(queue='briefing_jobs', durable=True)

    def do_work(ch, method, properties, body):
        """This function runs in a separate thread."""
        try:
            data = json.loads(body)
            user_id = data.get('userId')
            if user_id:
                run_job_for_user(user_id)
            else:
                print(" [!] Error: No userId in message")
        except Exception as e:
            print(f" [!] Error processing job: {e}")

        # Acknowledge the message from within the thread
        ch.basic_ack(delivery_tag=method.delivery_tag)
        print(f" [x] Worker finished and acknowledged job for user {user_id}")

    def callback(ch, method, properties, body):
        print(f" [x] Worker received job: {body.decode()}")
        thread = threading.Thread(target=do_work, args=(ch, method, properties, body))
        thread.start()

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='briefing_jobs', on_message_callback=callback)

    print(' [*] Worker is now waiting for messages.')
    channel.start_consuming()

if __name__ == '__main__':
    main()