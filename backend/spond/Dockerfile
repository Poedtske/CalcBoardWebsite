# Use an official Python runtime as a base image
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /app

# Copy the script and any required files (like .env) into the container
COPY spondApi.py .
COPY .env .

# Install dependencies
RUN pip install --no-cache-dir spond python-dotenv

# Run the script when the container starts
CMD ["python", "spondApi.py"]
