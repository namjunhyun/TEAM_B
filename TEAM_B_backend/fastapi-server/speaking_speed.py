from openai import OpenAI
from pyexpat.errors import messages

client = OpenAI()

def calculate_wpm(text: str, duration_seconds: float):
    """
    말의 빠르기 계산 함수
    :param text: 전체 발화 텍스트
    :param duration_seconds: 음성길이 (초)
    :return:
    """
    word_count = len(text.strip().split())
    duration_minutes = duration_seconds / 60

    if duration_minutes == 0:
        raise ValueError("발화 시간이 0초입니다.")

    wpm = word_count / duration_minutes
    return round(wpm, 2), word_count

def get_speaking_speed_feedback(text: str, wpm: float) -> str:
    """
    OpenAI API로 말 빠르기 피드백 생성 요청
    """
    prompt = f"""
다음은 사용자의 발화 텍스트입니다:

{text}

이 사용자의 발화 속도는 약 {wpm} WPM (Words Per Minute)입니다.
일반적인 대화 속도는 130~160 WPM입니다.

이 말의 빠르기에 대해 자연스럽고 구체적인 피드백을 작성해주세요.
"""
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {"role": "system", "content": "당신은 말하기 코치입니다. 사용자의 말 빠르기에 대한 피드백을 줍니다."},
            {"role": "user", "content": prompt},
        ],
        temperature=0.5,
        max_tokens=300,
    )

    return response.choices[0].message.content.strip()