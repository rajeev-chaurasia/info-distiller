import requests
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
    Takes a website URL, fetches the HTML, and uses the readability
    library to extract the main article text, stripping all HTML tags.
    """
    print(f"[Scraper Tool] Scraping and cleaning URL: {url}")
    try:
        response = requests.get(url, headers=HEADERS, timeout=15)
        response.raise_for_status()

        # Use readability-lxml to extract the main article content
        doc = Document(response.text)

        # Use BeautifulSoup to get clean text from the parsed HTML
        soup = BeautifulSoup(doc.summary(), 'html.parser')

        return soup.get_text(separator='\n', strip=True)

    except requests.exceptions.RequestException as e:
        return f"Error: Could not fetch the article from URL: {url}. Details: {e}"
    except Exception as e:
        return f"Error: An unexpected error occurred while processing the article. Details: {e}"

@tool("Content Summarizer Tool")
def summarization_tool(text: str) -> str:
    """
    Takes a block of text and returns a concise,
    bullet-point summary of its key points.
    """
    # First, check if the input text contains an error message from the scraper
    if text.strip().startswith("Error:"):
        return "Could not summarize the article because the content could not be fetched."

    prompt = f"""
    Summarize the following text in 3-5 clear and concise bullet points.
    Text:
    ---
    {text}
    ---
    Summary:
    """
    return llm.invoke(prompt)

@tool("Interest Query Generation Tool")
def interest_query_tool(topic: str) -> str:
    """
    Takes a simple user-provided topic (e.g., 'AI Startups') and generates a
    sophisticated, structured search query for a news API.
    """
    prompt = f"""
    You are an expert news analyst. A user wants to track news about the topic: '{topic}'.
    Your task is to generate a sophisticated search query string for a news API like NewsAPI.
    The query should be boolean, using OR operators to include related keywords and quoted phrases for exact matches.
    Include 3 to 5 highly relevant and diverse terms.

    Example Topic: 'Electric Cars'
    Example Output: '"electric vehicles" OR "Tesla" OR "EV charging" OR "solid-state batteries"'

    Topic: '{topic}'
    Output:
    """
    return llm.invoke(prompt)