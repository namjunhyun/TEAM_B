import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./main.css"; // 스타일시트 임포트

function Load() {
  const navigate = useNavigate();
  const [animate1, setAnimate1] = useState(false);
  const [animate2, setAnimate2] = useState(false);

  useEffect(() => {
    // 2.5초 후 로고 애니메이션 시작
    const timer1 = setTimeout(() => {
      setAnimate1(true);
    }, 2500);

    return () => clearTimeout(timer1);
  }, []);

  useEffect(() => {
    // 2초 후 Voice to Text 애니메이션 시작
    const timer2 = setTimeout(() => {
      setAnimate2(true);
    }, 2000);

    return () => clearTimeout(timer2);
  }, []);

  useEffect(() => {
    // 5초 후 업로드 페이지로 자동 이동
    const timer3 = setTimeout(() => {
      navigate("/upload");
    }, 5000);

    return () => clearTimeout(timer3);
  }, [navigate]);

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
      {/* | Voice to Text : animate2 */}
      <h2
        style={{
          color: "#F2C81B",
          fontFamily: "Cormorant Garamond, serif",
          fontWeight: 600,
          fontSize: "40px",
          position: "absolute",
          top: "40%",
          left: "48%",
          padding: "0px",
          margin: "0px",
          opacity: animate2 ? 0 : 1,
          transition: "all 0.4s ease-in-out",
        }}
      >
        | Voice to Text
      </h2>

      {/* Saymary 로고 : animate1 */}
      <h1
        style={{
          color: "#F2C81B",
          fontFamily: "Cormorant Garamond, serif",
          fontWeight: 600,
          fontSize: animate1 ? "60px" : "128px",
          position: "absolute",
          padding: "0px",
          margin: "0px",
          top: animate1 ? "7%" : "50%",
          left: animate1 ? "13%" : "50%",
          transform: animate1 ? "translate(0, 0)" : "translate(-50%, -50%)",
          opacity: 1,
          transition: "all 1.5s ease-in-out",
          cursor: "pointer",
        }}
        onClick={() => navigate("/upload")}
        title="업로드 페이지로 이동"
      >
        Saymary
      </h1>
    </div>
  );
}

export default Load;
