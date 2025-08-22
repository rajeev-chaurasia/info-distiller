from crewai import Task
from ..agents.agents import research_scout, writer

# Task 1: Scrape the article content
scrape_task = Task(
    description=(
        "Use your tool to scrape the full text content from the provided URL: {url}"
    ),
    expected_output="The full, clean text of the article, with no HTML tags.",
    agent=research_scout
)

# Task 2: Summarize the scraped content
summarize_task = Task(
    description=(
        "Read the article content you have been provided. Your task is to create a "
        "concise summary of the key points in 3-5 bullet points."
    ),
    expected_output="A summary of the article formatted as a bulleted list.",
    agent=writer,
    context=[scrape_task]
)