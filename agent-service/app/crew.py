from crewai import Crew, Process
from .agents.agents import research_scout, writer
from .tasks.tasks import scrape_task, summarize_task

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