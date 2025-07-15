FROM python:3.10-slim

# ffmpeg 설치 (whisper에서 필요)
RUN apt-get update && apt-get install -y ffmpeg

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY stt_server.py .

CMD ["python", "stt_server.py"]
