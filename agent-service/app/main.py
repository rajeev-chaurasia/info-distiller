from fastapi import FastAPI
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