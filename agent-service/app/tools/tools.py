import requests
from readability import Document
from bs4 import BeautifulSoup

def fetch_article_text(url: str) -> str:
    """
    Fetches the content from a URL and extracts the main article text.

    Args:
        url: The URL of the article to fetch.

    Returns:
        The clean, extracted text of the article.
    """
    try:
        # Send an HTTP GET request to the URL
        response = requests.get(url, timeout=10)
        # Raise an exception if the request was not successful (e.g., 404 Not Found)
        response.raise_for_status()

        # Use the readability library to parse the HTML content
        doc = Document(response.text)
        article_html = doc.summary()

        # Use BeautifulSoup to parse the clean HTML and extract the plain text
        soup = BeautifulSoup(article_html, 'html.parser')
        plain_text = soup.get_text()

        return plain_text

    except requests.exceptions.RequestException as e:
        print(f"Error fetching URL {url}: {e}")
        return f"Error: Could not fetch the article from the provided URL. {e}"
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return f"Error: An unexpected error occurred while processing the article. {e}"
