import vertexai
from vertexai.generative_models import GenerativeModel

PROJECT_ID = "info-distiller"
LOCATION = "us-central1"
LLM_MODEL = "gemini-2.0-flash-001"
vertexai.init(project=PROJECT_ID, location=LOCATION)

def generate_test_summary():
    """
    A simple function to test the connection to the Gemini model.
    """
    model = GenerativeModel(LLM_MODEL)

    # The prompt we are sending to the model.
    prompt = "In one short sentence, why is the sky blue?"

    # Send the prompt to the model and get the response.
    response = model.generate_content(prompt)

    # Return the text part of the response.
    return response.text

def summarize_text(text_content: str) -> str:
    """
    Uses the Gemini model to summarize a given piece of text.
    Args:
        text_content: The text to be summarized.
    Returns:
        The AI-generated summary.
    """
    model = GenerativeModel(LLM_MODEL)

    prompt = f"""
    You are an expert summarizer. Your task is to provide a concise, easy-to-read summary of the following article.
    The summary should be approximately 3-5 bullet points, capturing the main ideas.

    ARTICLE TEXT:
    ---
    {text_content}
    ---

    SUMMARY:
    """

    response = model.generate_content(prompt)
    return response.text
