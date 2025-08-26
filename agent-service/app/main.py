from fastapi import FastAPI
from .models.models import URLRequest

from .crew import run_summarization_crew

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
    Accepts a URL and uses the crew to scrape and summarize the content.
    """
    result = run_summarization_crew(str(request.url))
    final_summary = result.raw if result and hasattr(result, 'raw') else str(result)

    return {"summary": final_summary}