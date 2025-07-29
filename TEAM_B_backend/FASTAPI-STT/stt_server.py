import os
import json
import tempfile
import asyncio
import subprocess
from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
import httpx
import openai
import requests

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

CLOVA_STT_URL = os.getenv("CLOVA_INVOKE_URL")
CLOVA_API_KEY = os.getenv("CLOVA_API_KEY")
openai.api_key = os.getenv("OPENAI_API_KEY")

if not (CLOVA_STT_URL and CLOVA_API_KEY and openai.api_key):
    raise RuntimeError("환경변수가 설정되어 있지 않습니다.")

def send_to_spring_backend(user_id: int, summary: str):
    try:
        spring_url = "http://stt-backend:8080/api/summary"
        payload = {"user_id": user_id, "summaryText": summary}
        responses = requests.post(spring_url, json=payload)
        responses.raise_for_status()
    except Exception as e:
        print(f"Spring 서버 전송 실패: {e}")

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

async def call_clova_stt(wav_path: str) -> str:
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
                        "completion": "sync"
                    }), "application/json")
                }
                resp = await client.post(CLOVA_STT_URL, headers=headers, files=files)
        resp.raise_for_status()
        result_json = resp.json()
        text = result_json.get("text", "")
        if not text:
            segments = result_json.get("segments", [])
            text = " ".join(seg.get("textEdited", seg.get("text", "")) for seg in segments)
        return text
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

async def call_openai_feedback(text: str, prompt: str, temperature: float = 0.7) -> str:
    def sync_call():
        response = openai.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": "당신은 코칭 전문가입니다."},
                {"role": "user", "content": f"{prompt}\n\n{text}"},
            ],
            temperature=temperature,
            max_tokens=300,
        )
        return response.choices[0].message.content.strip()
    return await asyncio.to_thread(sync_call)

@app.post("/upload_feedback")
async def upload_feedback(
        file: UploadFile = File(...),
        situation: str = Form(...),
        audience: str = Form(...),
        style: str = Form(...)
):
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

    try:
        full_text = await call_clova_stt(wav_path)
    finally:
        try:
            os.remove(wav_path)
        except Exception:
            pass

    feedback_prompt = (
            f"상황: {situation}, 청중: {audience}, 스타일: {style}에 맞춰 다음 발화에 대한 피드백을 주세요. "
            "한줄평, 개선점 분석해 주세요. 그리고 gpt인걸 티안나게 이모티콘사용은 자제해주세요. 깔끔하게 부탁해요\n\n"
            + full_text
    )
    try:
        feedback = await call_openai_feedback(full_text, feedback_prompt, temperature=0.7)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OpenAI 피드백 실패: {e}")

    return JSONResponse({
        "original_text": full_text,
        "feedback": feedback,
    })

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

    try:
        full_text = await call_clova_stt(wav_path)
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
    send_to_spring_backend(user_id=1, summary=summaries["간단요약"])

    return JSONResponse({
        "original_text": full_text,
        "summaries": summaries,
    })
