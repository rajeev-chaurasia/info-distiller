#!/bin/bash

# Start the FastAPI server in the background
uvicorn app.main:app --host 0.0.0.0 --port 8000 &

# Start the RabbitMQ worker in the foreground
python -m app.worker