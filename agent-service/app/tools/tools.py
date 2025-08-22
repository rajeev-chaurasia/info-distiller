import requests
from readability import Document
from bs4 import BeautifulSoup

from crewai.tools import tool

def _fetch_article_text(url: str) -> str:
    """
    Internal function to fetch the content from a URL and extracts the main article text.

    Args:
        url: The URL of the article to fetch.

    Returns:
        The clean, extracted text of the article.
    """
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        doc = Document(response.text)
        article_html = doc.summary()
        soup = BeautifulSoup(article_html, 'html.parser')
        plain_text = soup.get_text()
        return plain_text
    except Exception as e:
        print(f"Error fetching or parsing URL {url}: {e}")
        return f"Error: Could not process the article from the URL. {e}"

# Article Scraper Tool
@tool("Article Scraper Tool")
def article_scraper_tool(url: str) -> str:
    """
    A tool that takes a website URL as input, fetches the content,
    and returns the clean, main article text. Ideal for scraping news articles,
    blog posts, and other text-heavy webpages.

    Args:
        url: The URL of the article to fetch.

    Returns:
        The clean, extracted text of the article.
    """
    return _fetch_article_text(url)