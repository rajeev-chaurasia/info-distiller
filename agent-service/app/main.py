from fastapi import FastAPI
from .models.models import URLRequest
from .tools import tools
from . import ai_core

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

# New endpoint to test our connection to Vertex AI
@app.get("/api/v1/test-ai")
async def test_ai_connection():
    """
    Calls the Gemini model with a test prompt and returns the response.
    """
    summary = ai_core.generate_test_summary()
    return {"summary": summary}

@app.post("/api/v1/summarize-url")
async def summarize_url(request: URLRequest):
    """
    Accepts a URL, fetches the article text, and returns an AI-generated summary.
    """
    article_text = tools.fetch_article_text(str(request.url))
    summary = ai_core.summarize_text(article_text)
    return {"summary": summary}