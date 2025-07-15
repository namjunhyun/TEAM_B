import os
from flask import Flask, request, jsonify, Response
import whisper
import openai
import json  # 추가

app = Flask(__name__)
UPLOAD_FOLDER = "uploaded_files"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# 도커 환경에서 주입된 환경변수 사용
openai.api_key = os.getenv("OPENAI_API_KEY")
if not openai.api_key:
    raise ValueError("OPENAI_API_KEY가 설정되지 않았습니다.")  # 디버깅에 도움됨

# whisper 모델 로드
model = whisper.load_model("base")

def transcribe_audio(file_path):
    result = model.transcribe(file_path, language="ko")
    return result["text"]

def summarize_text(text, prompt):
    response = openai.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "system", "content": "당신은 요약 전문가입니다."},
            {"role": "user", "content": prompt + "\n\n" + text}
        ],
        temperature=0.5,
        max_tokens=300
    )
    return response.choices[0].message.content.strip()

@app.route("/upload_stt_summary", methods=["POST"])
def upload_stt_summary():
    if 'file' not in request.files:
        return jsonify({"error": "file 필드가 필요합니다."}), 400

    audio_file = request.files['file']
    if audio_file.filename == '':
        return jsonify({"error": "파일이 선택되지 않았습니다."}), 400

    save_path = os.path.join(UPLOAD_FOLDER, audio_file.filename)
    audio_file.save(save_path)

    # 음성 → 텍스트 변환
    text = transcribe_audio(save_path)

    # 3가지 요약 생성
    summaries = {
        "간단요약": summarize_text(text, "간단하게 요약해줘."),
        "상세요약": summarize_text(text, "상세하게 요약해줘."),
        "키워드요약": summarize_text(text, "중요 키워드만 뽑아줘."),
    }

    result = {
        "original_text": text,
        "summaries": summaries
    }

    # 한글 깨짐 없이 JSON 반환
    return Response(json.dumps(result, ensure_ascii=False), mimetype='application/json')


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
