from fastapi import FastAPI
from .models.models import URLRequest

from crewai import Crew, Process
from .agents.agents import research_scout, writer
from .tasks.tasks import scrape_task, summarize_task

# Create an instance of the FastAPI application
app = FastAPI(
    title="Info-Distiller Agent Service",
    description="The AI core for processing and summarizing content.",
    version="0.0.1",
)

# Define a health check endpoint
@app.get("/health")
def read_health():
    """
    Health check endpoint. Returns the status of the service.
    """
    return {"status": "ok"}

@app.post("/api/v1/summarize-url")
async def summarize_url(request: URLRequest):
    """
    Accepts a URL and kicks off a Crew to scrape and summarize the content.
    """
    # Create a dictionary of inputs for the task
    inputs = {'url': str(request.url)}

    # Assemble the crew with our agents and tasks
    # The process is sequential, meaning tasks will be executed one after another
    summarization_crew = Crew(
        agents=[research_scout, writer],
        tasks=[scrape_task, summarize_task],
        process=Process.sequential,
        verbose=True
    )

    # Kick off the crew's work with the provided inputs
    result = summarization_crew.kickoff(inputs=inputs)
    final_summary = result.raw if result and hasattr(result, 'raw') else str(result)

    return {"summary": final_summary}