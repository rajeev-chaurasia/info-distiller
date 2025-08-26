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

# Task 2: Summarize the content
summarize_task = Task(
    description=(
        "Use your 'Content Summarizer Tool' to create a concise summary "
        "of the provided article content in 3-5 bullet points."
    ),
    expected_output="A summary of the article formatted as a bulleted list.",
    agent=writer,
    context=[scrape_task] # The context is still passed from the first task
)