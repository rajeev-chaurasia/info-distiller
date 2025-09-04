import os
import psycopg2
import requests
from datetime import datetime, timedelta

from .crew import run_summarization_crew


def fetch_articles_for_interest(query: str):
    """
    Fetches news articles for a given query from NewsAPI.
    """
    api_key = os.getenv("NEWS_API_KEY")
    if not api_key:
        print("[Daily Job] Error: NEWS_API_KEY environment variable not set.")
        return []

    start_date = (datetime.now() - timedelta(days=7)).strftime('%Y-%m-%d')

    url = "https://newsapi.org/v2/everything"
    params = {
        'q': query,
        'from': start_date,
        'sortBy': 'popularity',
        'language': 'en',
        'apiKey': api_key
    }

    try:
        response = requests.get(url, params=params)
        response.raise_for_status()
        data = response.json()
        articles = data.get("articles", [])
        print(f"\n[Daily Job] Found {len(articles)} articles for query: '{query}'")
        return articles
    except requests.exceptions.RequestException as e:
        print(f"[Daily Job] Error fetching from NewsAPI for query '{query}': {e}")
        return []


def run_job_for_user(user_id: int):
    """
    Runs the full briefing generation process for a single user.
    """
    print(f"[Job] Starting briefing generation for user_id: {user_id}")
    conn = None
    try:
        conn = psycopg2.connect(
            host=os.getenv("DB_HOST"),
            dbname=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD")
        )
        cur = conn.cursor()

        cur.execute("SELECT query_template FROM interests WHERE user_id = %s", (user_id,))
        interests = cur.fetchall()

        print(f"[Job] Found {len(interests)} interests for user {user_id}.")
        for interest in interests:
            query = interest[0]
            articles = fetch_articles_for_interest(query)

            for article in articles[:3]:  # Process top 3 articles per interest
                print(f"\n--- [Job] Processing Article ---")
                print(f"  Title: {article['title']}")

                try:
                    summary_result = run_summarization_crew(article['url'])
                    final_summary = summary_result.raw if summary_result and hasattr(summary_result, 'raw') else str(
                        summary_result)

                    if "error" not in final_summary.lower() and "unable to" not in final_summary.lower():
                        insert_query = """
                                       INSERT INTO selections (user_id, article_url, article_title, summary, picked_for_date)
                                       VALUES (%s, %s, %s, %s, %s)
                                       ON CONFLICT
                                           (user_id, article_url, picked_for_date)
                                           DO NOTHING; \
                                       """
                        cur.execute(insert_query,
                                    (user_id, article['url'], article['title'], final_summary, datetime.now().date()))
                        conn.commit()
                        print(f"  ✅ Summary saved to database for user {user_id}.")
                    else:
                        print(f"  ❌ Skipping save for failed summary.")
                except Exception as e:
                    print(f"  !! Crew failed for article: {article['url']}. Error: {e}")

    except Exception as e:
        print(f"[Job] A critical error occurred for user {user_id}: {e}")
    finally:
        if conn:
            conn.close()

    try:
        print(f"[Job] Notifying gateway that job for user {user_id} is complete.")
        # The URL must match the service name in docker-compose.yml
        gateway_url = "http://gateway:8082/api/briefing/notify"

        # This endpoint is public, so no token is needed for this simple service-to-service call
        requests.post(gateway_url, json={"userId": user_id})
        print(f"[Job] Notification sent successfully for user {user_id}.")
    except Exception as e:
        print(f"[Job] CRITICAL: Failed to send completion notification for user {user_id}. Error: {e}")
