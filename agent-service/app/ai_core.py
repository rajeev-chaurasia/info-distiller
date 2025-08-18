import vertexai
from vertexai.generative_models import GenerativeModel

PROJECT_ID = "info-distiller"
LOCATION = "us-central1"
vertexai.init(project=PROJECT_ID, location=LOCATION)

def generate_test_summary():
    """
    A simple function to test the connection to the Gemini model.
    """
    model = GenerativeModel("gemini-2.0-flash-001")

    # The prompt we are sending to the model.
    prompt = "In one short sentence, why is the sky blue?"

    # Send the prompt to the model and get the response.
    response = model.generate_content(prompt)

    # Return the text part of the response.
    return response.text