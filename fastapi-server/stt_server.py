import os
import json
import tempfile
import asyncio
import subprocess
from http.client import responses

import requests

from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from pydantic import BaseModel
from speaking_speed import calculate_wpm, get_speaking_speed_feedback
from pause_analysis import analyze_pauses, get_pause_feedback
from typing import List

import httpx
import openai

load_dotenv()

app = FastAPI()

# 프론트 서버 연동
origins = ["https://saymary-frontend.vercel.app"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins, # 프론트 주소 허용
    allow_credentials=True,
    allow_methods=["*"], # GET, POST 등 허용
    allow_headers=["*"], # 모든 헤더 허용
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


CLOVA_STT_URL = os.getenv("CLOVA_INVOKE_URL")
CLOVA_API_KEY = os.getenv("CLOVA_API_KEY")
openai.api_key = os.getenv("OPENAI_API_KEY")


# 예외처리
if not (CLOVA_STT_URL and CLOVA_API_KEY and openai.api_key):
    raise RuntimeError("환경변수가 설정되어 있지 않습니다.")

# Spring 전송 함수
def send_to_spring_backend(
        user_id: int,
        original_filename: str,
        transcript: str,
        summaries: dict
):
    try:
        spring_url = "http://mainspring-server:8080/api/summary"
        payload = {
            "user_id": user_id,
            "originalFilename": original_filename,
            "transcript": transcript,
            "summary1": summaries["간단요약"],
            "summary2": summaries["상세요약"],
            "summary3": summaries["키워드요약"]
        }
        responses = requests.post(spring_url, json=payload)
        responses.raise_for_status()
    except Exception as e:
        print(f"Spring 서버 전송 실패: {e}")

# m4a파일 wav로 변환
def convert_m4a_to_wav(m4a_path: str, wav_path: str):
    try:
        subprocess.run(
            ["ffmpeg", "-y", "-i", m4a_path, wav_path],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
        )
    except subprocess.CalledProcessError as e:
        raise RuntimeError(f"ffmpeg 변환 실패: {e.stderr.decode()}")

## ffprobe 기반 길이 측정 함수 (정확한 오디오 길이 측정)
def get_audio_duration(path: str) -> float:
    try:
        result = subprocess.run(
            ["ffprobe", "-v", "error", "-show_entries", "format=duration","-of", "default=noprint_wrappers=1:nokey=1", path],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        duration_str = result.stdout.strip()
        return round(float(duration_str), 2)
    except Exception as e:
        print(f"오디오 길이 측정 실패: {e}")
        return 0.0

# 말하기 속도 측정
class SpeedRequest(BaseModel):
    text: str
    duration_seconds: float

@app.post("/analyze_speed")
async def analyze_speed(req: SpeedRequest):
    try:
        wpm, word_count = calculate_wpm(req.text, req.duration_seconds)
        feedback = get_speaking_speed_feedback(req.text, wpm)

        return {
            "word_count": word_count,
            "duration_seconds": req.duration_seconds,
            "wpm": wpm,
            "feedback": feedback
        }
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# 말하기 공백 측정
class Segment(BaseModel):
    text: str
    start: float
    end: float

class PauseRequest(BaseModel):
    text: str
    segments: List[Segment]

@app.post("/analyze_pause")
async def analyze_pause(req: PauseRequest):
    try:
        segment_dicts = [segment.dict() for segment in req.segments]
        stats = analyze_pauses(segment_dicts)
        feedback = get_pause_feedback(req.text, stats)

        return {
            "pause_stats": stats,
            "feedback": feedback
        }
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

# 호출과 기타설정
async def call_clova_stt(wav_path: str) -> tuple[str, list]:
    headers = {
        "Accept": "application/json",
        "X-CLOVASPEECH-API-KEY": CLOVA_API_KEY,
    }
    try:
        async with httpx.AsyncClient() as client:
            with open(wav_path, "rb") as wav_file:
                files = {
                    "media": ("file.wav", wav_file, "audio/wav"),
                    "params": (None, json.dumps({
                        "language": "ko-KR",
                        "completion": "sync",
                        "fullText": True,
                        "wordAlignment": True # 타임스탬프 옵션 추가
                    }), "application/json")
                }
                resp = await client.post(CLOVA_STT_URL, headers=headers, files=files)

        resp.raise_for_status()
        result_json = resp.json()
        segments = result_json.get("segments", []) # 속도, 공백 응답 위해 추가
        text = result_json.get("text", "")
        if not text:
            text = " ".join(seg.get("textEdited", seg.get("text", "")) for seg in segments)
        return text, segments
    except httpx.HTTPStatusError as e:
        error_detail = await e.response.aread()
        raise HTTPException(status_code=500, detail=f"Clova STT 호출 실패: {e.response.status_code} - {error_detail.decode()}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Clova STT 호출 실패: {e}")

async def call_openai_summary(text: str, prompt: str, temperature: float = 0.5) -> str:
    def sync_call():
        response = openai.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": "당신은 요약 전문가입니다."},
                {"role": "user", "content": f"{prompt}\n\n{text}"},
            ],
            temperature=temperature,
            max_tokens=300,
        )
        return response.choices[0].message.content.strip()
    return await asyncio.to_thread(sync_call)

@app.post("/upload_stt_summary")
async def upload_stt_summary(file: UploadFile = File(...)):
    suffix = os.path.splitext(file.filename)[1].lower()

    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as tmp:
            contents = await file.read()
            tmp.write(contents)
            tmp_path = tmp.name
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"파일 저장 실패: {e}")

    wav_path = tmp_path

    if suffix == ".m4a":
        try:
            with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as wav_tmp:
                wav_path = wav_tmp.name
            convert_m4a_to_wav(tmp_path, wav_path)
        except Exception as e:
            try:
                os.remove(tmp_path)
            except Exception:
                pass
            try:
                os.remove(wav_path)
            except Exception:
                pass
            raise HTTPException(status_code=500, detail=f"m4a → wav 변환 실패: {e}")
        finally:
            try:
                os.remove(tmp_path)
            except Exception:
                pass

    # STT 호출 결과 받기
    try:
        full_text, segments = await call_clova_stt(wav_path)

        # 정확한 전체 오디오 길이 계산
        duration_seconds = get_audio_duration(wav_path)

    finally:
        try:
            os.remove(wav_path)
        except Exception:
            pass

    prompts = {
        "간단요약": "간단하게 요약해줘.",
        "상세요약": "상세하게 요약해줘.",
        "키워드요약": "중요 키워드만 뽑아줘.",
    }
    tasks = [call_openai_summary(full_text, p) for p in prompts.values()]
    try:
        results = await asyncio.gather(*tasks)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OpenAI 요약 실패: {e}")

    summaries = dict(zip(prompts.keys(), results))

    # 속도, 공백 측정을 위한 선언
    wpm, word_count = calculate_wpm(full_text, duration_seconds)
    speed_feedback = get_speaking_speed_feedback(full_text, wpm)

    pause_stats = analyze_pauses(segments)
    pause_feedback = get_pause_feedback(full_text, pause_stats)

    # Spring 호출을 위한 함수 넣기
    send_to_spring_backend(
        user_id=1,
        original_filename=file.filename,
        transcript=full_text,
        summaries=summaries
    )

    return JSONResponse({
        "original_text": full_text,
        "summaries": summaries,
        "speed_analysis": {
            "word_count": word_count,
            "duration_seconds": duration_seconds,
            "wpm": wpm,
            "feedback": speed_feedback
        },
        "pause_analysis": {
            "pause_stats": pause_stats,
            "feedback": pause_feedback
        }
    })