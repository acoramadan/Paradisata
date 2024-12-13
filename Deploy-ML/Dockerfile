FROM python:3.10-slim

# Menentukan variabel lingkungan
ENV PYTHONUNBUFFERED True

# Set work directory
ENV APP_HOME /app
WORKDIR $APP_HOME

# Install alat build dan library pendukung
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    build-essential \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy semua file ke dalam container
COPY . ./

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose port untuk aplikasi
EXPOSE 8080

# Gunakan Gunicorn untuk menjalankan aplikasi
CMD ["gunicorn", "-b", "0.0.0.0:8080", "app:app"]
