from langchain_google_vertexai import VertexAI

# Define the shared LLM instance in this central file
llm = VertexAI(model_name="gemini-2.0-flash-001")