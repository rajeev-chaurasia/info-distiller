from crewai import Crew, Process
from .agents.agents import research_scout, writer, interest_analyst
from .tasks.tasks import scrape_task, summarize_task, generate_query_task

def run_summarization_crew(url: str):
    """
    Initializes and kicks off the summarization crew for a given URL.
    """
    inputs = {'url': url}

    summarization_crew = Crew(
        agents=[research_scout, writer],
        tasks=[scrape_task, summarize_task],
        process=Process.sequential,
        verbose=True
    )

    result = summarization_crew.kickoff(inputs=inputs)
    return result

def run_interest_generation_crew(topic: str):
    """
    Initializes and kicks off the interest generation crew for a given topic.
    """
    inputs = {'topic': topic}

    interest_crew = Crew(
        agents=[interest_analyst],
        tasks=[generate_query_task],
        process=Process.sequential,
        verbose=True
    )

    result = interest_crew.kickoff(inputs=inputs)
    return result