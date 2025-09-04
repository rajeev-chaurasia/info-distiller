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

# Copy the supervisord configuration file into the container
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose the port the server will run on
EXPOSE 8000

# The command to run supervisord, which will in turn manage our processes
CMD ["/usr/local/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]