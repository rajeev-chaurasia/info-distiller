from crewai import Agent

from ..config import llm
# Import the tool we created
from ..tools.tools import article_scraper_tool, summarization_tool, interest_query_tool

# Agent 1: The Research Scout
# This agent is responsible for finding and scraping information from the web.
research_scout = Agent(
    role="Senior Research Analyst",
    goal="Find and extract the full text content from a given article URL.",
    backstory=(
        "You are an expert at navigating the web to find and retrieve "
        "information. You are skilled at identifying the core content of a webpage "
        "and stripping away all the unnecessary fluff like ads and navigation bars."
    ),
    tools=[article_scraper_tool],
    llm=llm,
    allow_delegation=False,
    verbose=True
)

# Agent 2: The Expert Writer
# This agent is responsible for taking text content and summarizing it.
writer = Agent(
    role="Expert Content Summarizer",
    goal="Craft a concise, easy-to-read, bullet-point summary of a given text.",
    backstory=(
        "You are a renowned content strategist, known for your ability to distill "
        "complex topics into clear, insightful summaries. You transform dense text "
        "into perfectly formatted, bite-sized bullet points."
    ),
    tools=[summarization_tool],
    llm=llm,
    allow_delegation=False,
    verbose=True
)

# Agent 3: The Interest Analyst
# This agent is responsible for generating a sophisticated news API query from a simple topic.
interest_analyst = Agent(
    role="Expert Interest Analyst",
    goal="Generate a sophisticated news API query based on a simple user topic.",
    backstory=(
        "You are a world-class researcher, skilled at identifying the key "
        "terms and concepts related to any topic. You can instantly brainstorm "
        "a comprehensive search query that will find the most relevant news."
    ),
    tools=[interest_query_tool],
    llm=llm,
    verbose=True
)