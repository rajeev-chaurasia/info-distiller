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
    Connects to the database, fetches interests, fetches articles, and summarizes them.
    """
    try:
        conn = psycopg2.connect(
            host=os.getenv("DB_HOST"),
            dbname=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD")
        )
        cur = conn.cursor()
        print("[Daily Job] Successfully connected to the database.")

        cur.execute("SELECT query_template FROM interests")
        interests = cur.fetchall()

        print(f"[Daily Job] Found {len(interests)} interests to process.")
        for interest in interests:
            query = interest[0]
            articles = fetch_articles_for_interest(query)

            for article in articles[:3]:
                print(f"\n--- Processing Article ---")
                print(f"  Title: {article['title']}")
                print(f"  URL: {article['url']}")

                # --- ADD ERROR HANDLING AROUND THE CREW CALL ---
                try:
                    # Use our agentic crew to get the summary
                    summary = run_summarization_crew(article['url'])
                    final_summary = summary.raw if summary and hasattr(summary, 'raw') else str(summary)
                    print(f"  Summary:\n{final_summary}")
                except Exception as e:
                    # If the crew fails for any reason, log it and continue
                    print(f"  !! Crew failed for this article: {e}")
                    print(f"  !! Moving to the next article.")

                print(f"--------------------------")

        cur.close()
        conn.close()

    except Exception as e:
        print(f"[Daily Job] Error in run_job: {e}")


if __name__ == "__main__":
    run_job()