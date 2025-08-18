# Use the official slim Python 3.12 image as a base
FROM python:3.12-slim

# Set the working directory inside the container to /app
WORKDIR /app

# Copy the requirements file first to leverage Docker's build cache
COPY requirements.txt .

# Install uv, then use uv to install the rest of the dependencies
RUN pip install uv
RUN uv pip install --system -r requirements.txt

# Copy the application source code into the container
COPY ./app ./app

# Expose the port the server will run on
EXPOSE 8000

# The command to run the Uvicorn server when the container starts
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]