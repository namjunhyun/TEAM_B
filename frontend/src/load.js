import React, { useEffect, useState } from "react";
import "./index.css"; // CSS 파일을 import 합니다.

function Load() {
  const [animate1, setAnimate1] = useState(false);
  const [animate2, setAnimate2] = useState(false);

  useEffect(() => {
    // 1.5초 후 애니메이션 시작
    const timer = setTimeout(() => {
      setAnimate1(true);
    }, 1500);

    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    // 3초 후 애니메이션 시작
    const timer = setTimeout(() => {
      setAnimate2(true);
    }, 3000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div
      style={{
        backgroundColor: "#00492C",
        height: "100vh",
        display: "flex",
        padding: "0px",
        margin: "0px",
        justifyContent: "center",
        alignItems: "center",
        position: "relative",
      }}
    >
      {/* Lingro+ 로고 : animate1 */}
      <h1
        style={{
          color: "#F2C81B",
          fontFamily: "Cormorant Garamond, serif",
          fontSize: animate1 ? "60px" : "128px",
          position: "absolute",
          padding: "0px",
          margin: "0px",
          top: animate1 ? "7%" : "50%",
          left: animate1 ? "13%" : "50%",
          transform: animate1 ? "translate(0, 0)" : "translate(-50%, -50%)",
          opacity: 1,
          transition: "all 1.5s ease-in-out",
        }}
      >
        Lingro+
      </h1>
      {/* 노란 채팅 박스 : animate2 */}
      <div
        style={{
          backgroundColor: "#FFFCE4",
          position: "absolute",
          borderRadius: "10px",
          top: "20%",
          left: "12%",
          width: "88%",
          height: "80%",
          opacity: animate2 ? 1 : 0,
          transition: "all 0.3s ease-in-out",
        }}
      >
        <h1
          style={{
            color: "#00492C",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 400,
            fontSize: "24px",
            paddingTop: "30px",
            margin: "0px",
            paddingLeft: "100px",
          }}
        >
          파일명 : 알아서 AI가 요약해준대로 임시로 지정
        </h1>
        <h2
          style={{
            color: "#00492C",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 400,
            fontSize: "15px",
            paddingLeft: "100px",
            paddingTop: "0px",
            margin: "0px",
          }}
        >
          2025.07.07 13:05 {/* 현재 시간 값 받아오기 */}
        </h2>
        <p
          style={{
            color: "#00492C",
            backgroundColor: "#ECEAD5",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 300,
            fontSize: "18px",
            marginLeft: "100px",
            marginRight: "100px",
            padding: "20px",
            borderRadius: "10px",
          }}
        >
          재택근무는 코로나19 팬데믹을 계기로 빠르게 확산된 근무 형태이다.
          직원들은 출퇴근 시간이 사라지면서 더 많은 여유 시간을 확보할 수 있게
          되었다. 이는 워라밸(Work-Life Balance) 향상에 긍정적인 영향을 주었다.
          또한, 자율적인 시간 관리가 가능해져 개인의 집중력이 오히려 높아지기도
          한다. 기업 입장에서는 사무실 운영비용 절감 등의 경제적 이점이
          존재한다. 반면, 팀원 간의 소통이 부족해지며 협업 효율이 낮아지는
          경우도 있다. 물리적 거리감은 심리적 거리감으로 이어져 조직 소속감을
          약화시킬 수 있다. 특히 신입사원의 경우 적응이 어렵고 피드백이 늦어
          성장이 더뎌질 수 있다. 업무와 사생활의 경계가 모호해지면서 오히려
          스트레스를 유발하기도 한다. 사이버 보안 및 데이터 보호 문제도
          재택근무의 큰 과제로 남아 있다. 일부 기업은 하이브리드 근무 형태를
          도입하여 장단점을 조율하고 있다. 기술 인프라와 커뮤니케이션 도구의
          발전은 원격 협업을 점차 수월하게 만들고 있다. 재택근무는 직무의 특성과
          개인의 성향에 따라 효과가 달라질 수 있다. 따라서 일률적인 정책보다는
          유연한 제도 설계가 필요하다. 결론적으로 재택근무는 미래 업무 환경의
          중요한 축으로 자리 잡아가고 있다. {/* 값을 어떻게 받아오지 */}
        </p>
      </div>
    </div>
  );
}

export default Load;
