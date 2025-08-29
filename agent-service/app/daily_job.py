import os
import psycopg2
import requests
from datetime import datetime, timedelta

from .crew import run_summarization_crew


def fetch_articles_for_interest(query: str):
    """
    Fetches news articles for a given query from NewsAPI.
    """
    # ... (this function is correct and does not need changes)
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


def run_job():
    """
    Connects to the database, fetches interests, fetches articles, summarizes them,
    and saves the results back to the database.
    """
    conn = None  # Initialize conn to None
    try:
        conn = psycopg2.connect(
            host=os.getenv("DB_HOST"),
            dbname=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD")
        )
        cur = conn.cursor()
        print("[Daily Job] Successfully connected to the database.")

        # For now, we are running the job for all users.
        # In the future, this could be parameterized.
        cur.execute("SELECT id, query_template FROM interests")
        interests = cur.fetchall()

        print(f"[Daily Job] Found {len(interests)} interests to process.")
        for interest in interests:
            interest_id = interest[0]
            query = interest[1]
            articles = fetch_articles_for_interest(query)

            for article in articles[:3]:
                print(f"\n--- Processing Article ---")
                print(f"  Title: {article['title']}")
                print(f"  URL: {article['url']}")

                try:
                    summary_result = run_summarization_crew(article['url'])
                    final_summary = summary_result.raw if summary_result and hasattr(summary_result, 'raw') else str(
                        summary_result)

                    # --- SAVE TO DATABASE ---
                    # Only save if the summary is not an error message
                    if "error" not in final_summary.lower() and "unable to" not in final_summary.lower():
                        insert_query = """
                                       INSERT INTO selections (user_id, article_url, article_title, summary, picked_for_date)
                                       VALUES (%s, %s, %s, %s, %s)
                                       ON CONFLICT
                                           (user_id, article_url, picked_for_date)
                                           DO NOTHING; \
                                       """
                        # We'll hardcode user_id to 1 for now
                        cur.execute(insert_query,
                                    (1, article['url'], article['title'], final_summary, datetime.now().date()))
                        conn.commit()
                        print(f"  ✅ Summary saved to database.")
                    else:
                        print(f"  ❌ Skipping save for failed summary.")

                except Exception as e:
                    print(f"  !! Crew failed for this article: {e}")

    except Exception as e:
        print(f"[Daily Job] A critical error occurred in run_job: {e}")
    finally:
        if conn:
            cur.close()
            conn.close()


if __name__ == "__main__":
    run_job()
