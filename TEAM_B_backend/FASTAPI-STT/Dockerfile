FROM python:3.10-slim

RUN apt-get update && apt-get install -y ffmpeg && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY stt_server.py .

# CMD ["uvicorn", "stt_server:app", "--host", "0.0.0.0", "--port", "8081"]

# 디버그 (오류 체크용)
# 환경 변수 설정 (선택 사항)
ENV LOG_LEVEL=debug

# 실제 FastAPI 실행 명령
CMD ["uvicorn", "stt_server:app", "--host", "0.0.0.0", "--port", "8081", "--log-level", "debug"]
