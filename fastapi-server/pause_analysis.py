
from openai import OpenAI


def analyze_pauses(segments: list, max_pause_threshold: float = 10.0):
    """
    Clova Speech STT의 segments 리스트를 받아 pause 통계를 게산한다.
    0.3초 이상 10초 이하의 멈춤만 pause로 간주한다. (일반적인 말하기 기준, 상한선 안잡으니 너무 커지는 오류 발생)
    segmnets = [{"text": "...", "start": float, "end": float}, ...]
    """
    pauses = []
    total_silence = 0

    for i in range(1, len(segments)):
        prev_end = segments[i - 1]["end"]
        curr_start = segments[i]["start"]
        pause = curr_start - prev_end
        if 0.3 < pause < max_pause_threshold: # 0.3초 이상 10초 이하만 pause로 간주
            pauses.append(pause)
            total_silence += pause

    return {
        "pause_count": len(pauses),
        "avg_pause_length": round(sum(pauses) / len(pauses), 2) if pauses else 0,
        "total_silence": round(total_silence, 2)
    }

def get_pause_feedback(text: str, stats: dict) -> str:
    """
    pause 통계 정보를 기반으로 OpenAI에게 자연이 피드백을 요청한다.
    """
    import openai
    prompt = f"""
사용자의 말하기 텍스트:
{text}
Pause 분석 결과:
- 멈춤 횟수: {stats['pause_count']}회
- 평균 멈춤 시간: {stats['avg_pause_length']}초
- 전체 무음 시간: {stats['total_silence']}초

위 정보를 기반으로, 말하는 습관에 대한 피드백을 자연스럽게 작성해주세요.
"""
    client = OpenAI()

    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {"role": "system", "content": "댱신은 말하기 피드백 전문가입니다."},
            {"role": "user", "content": prompt}
        ],
        temperature=0.5,
        max_tokens=300
    )

    return response.choices[0].message.content
