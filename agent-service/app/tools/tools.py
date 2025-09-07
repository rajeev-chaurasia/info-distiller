import requests
import os
from crewai.tools import tool
from ..config import llm
from bs4 import BeautifulSoup
from readability import Document

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36'
}


@tool("Article Scraper Tool")
def article_scraper_tool(url: str) -> str:
    """
    Takes a website URL, passes it to the ScrapingAnt API with browser rendering enabled,
    and returns the clean, main article text.
    """
    api_key = os.getenv("SCRAPINGANT_API_KEY")
    scrape_url = "https://api.scrapingant.com/v2/general"

    params = {
        'url': url,
        'x-api-key': api_key,
        'browser': 'true',  # Enable browser rendering
    }
    print(f"[Scraper Tool] Requesting browser-rendered content for: {url}")
    try:
        response = requests.get(scrape_url, params=params, timeout=120)
        response.raise_for_status()

        doc = Document(response.text)
        soup = BeautifulSoup(doc.summary(), 'html.parser')
        return soup.get_text(separator='\n', strip=True)
    except requests.exceptions.RequestException as e:
        return f"Error: Could not fetch the article from URL: {url}. Details: {e}"
    except Exception as e:
        return f"Error: An unexpected error occurred while processing the article. Details: {e}"


@tool("Content Summarizer Tool")
def summarization_tool(text: str) -> str:
    """
    Takes a block of text and returns a concise, bullet-point summary of its key points.
    """
    if text.strip().startswith("Error:"):
        return "Could not summarize the article because the content could not be fetched."

    prompt = f"""
    You are an expert content summarizer. Your task is to analyze the provided text and create a concise, easy-to-read summary in 3-5 bullet points.

    Instructions:
    1. First, carefully read the text to identify the main topic and key arguments.
    2. IMPORTANT: Before summarizing, you MUST filter out and ignore any irrelevant boilerplate content. This includes, but is not limited to:
        - Error messages (e.g., "Article Scraper Tool failed to extract content").
        - Cookie consent banners or paywall notifications.
        - Calls to action (e.g., "leave a comment," "sign up for our newsletter," "subscribe").
        - Author biographies or "More Great Stories" sections that are not part of the main article.
    3. If, after filtering, there is no meaningful article content left to summarize, you MUST return only the text: "Error: No valid article content found to summarize."
    4. Otherwise, create a summary of the key points as a bulleted list.

    Text to Summarize:
    ---
    {text}
    ---
    Summary:
    """
    return llm.invoke(prompt)


@tool("Interest Query Generation Tool")
def interest_query_tool(topic: str) -> str:
    """
    Takes a simple user-provided topic and generates a
    sophisticated, structured search query for a news API.
    """
    prompt = f"""
    You are an expert news analyst. A user wants to track news about the topic: '{topic}'.
    Your task is to generate a sophisticated search query string for a news API like NewsAPI.
    The query should be a boolean query using OR operators to include related keywords 
    and quoted phrases for exact matches. Include 3 to 5 highly relevant and diverse terms.

    Example Topic: 'Electric Cars'
    Example Output: '"electric vehicles" OR "Tesla" OR "EV charging" OR "solid-state batteries"'

    Topic: '{topic}'
    Output:
    """
    return llm.invoke(prompt)