import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Newpw() {
  const navigate = useNavigate();
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");

  const handlePasswordChange = (e) => {
    const { value } = e.target;
    setPassword(value);
    if (value.length > 0 && value.length < 6) {
      setPasswordError("Password must be at least 6 characters long.");
    } else {
      setPasswordError("");
    }
  }; // 비밀번호 input에 최소 6자 이상 입력
  const handleNewpw = async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");

    console.log("보낼 token:", token); // 반드시 확인

    if (!token) {
      alert("토큰이 유효하지 않습니다.");
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/user/reset-password",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            token: token,
            newPassword: password,
          }),
        }
      );

      const text = await response.text();
      console.log("응답 내용:", text);

      if (response.ok && text.includes("변경")) {
        alert("비밀번호가 성공적으로 변경되었습니다!");
        navigate("/login");
      } else {
        alert("비밀번호 변경 실패: " + text);
      }
    } catch (error) {
      alert("서버 오류 발생");
      console.error(error);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        width: "100%",
        height: "100vh",
        backgroundColor: "#00492C",
      }}
    >
      {/* 계정 생성 페이지 */}
      <h1
        style={{
          position: "absolute",
          bottom: "85%",
          left: "-20%",
          color: "#F2C81B",
          fontSize: "50px",
          marginBottom: 0,
          width: "100%",
          textAlign: "center",
        }}
      >
        Wipe the slate clean :)
      </h1>

      {/* 회원가입 폼 박스 */}
      <div
        style={{
          position: "absolute",
          bottom: "0",
          left: "50%",
          transform: "translateX(-50%)",
          width: "90%", // 전체 너비의 90%
          maxWidth: "500px", // 최대 너비 제한
          height: "60vh", // 전체 높이의 60%
          padding: "5vw", // 반응형 여백
          backgroundColor: "#FFFCE4",
          borderRadius: "10px",
          boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
          borderTopLeftRadius: "30px",
          borderTopRightRadius: "30px",
          borderBottomLeftRadius: "0px",
          borderBottomRightRadius: "0px",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
        }}
      >
        {/* pw 인풋 */}
        <h3
          style={{
            marginBottom: "5px",
            marginTop: "10px",
            fontSize: "1.2rem",
            marginLeft: "18%",
          }}
        >
          Password
        </h3>
        <input
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={handlePasswordChange}
          style={{
            marginBottom: "10px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: "#FFFCE4",
            border: "solid 2px #C7C29B",
            padding: "10px",
            width: "300px",
            marginLeft: "18%",
          }}
        />
        {passwordError && (
          <p
            style={{
              color: "red",
              fontSize: "0.8rem",
              marginLeft: "18%",
              marginTop: "0",
              marginBottom: "15px",
              fontFamily: "Noto Sans KR, sans-serif",
            }}
          >
            {passwordError}
          </p>
        )}

        {/* 제출 버튼 */}
        <button
          onClick={handleNewpw}
          disabled={!password || passwordError}
          style={{
            padding: "12px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: !password || passwordError ? "#C7C29B" : "#00492C",
            color: "white",
            border: "none",
            cursor: !password || passwordError ? "not-allowed" : "pointer",
            marginLeft: "18%",
            marginTop: "10px",
            width: "320px",
          }}
        >
          Submit
        </button>

        {/* 계정 등록 링크 (sign up) */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            paddingTop: "20px",
          }}
        >
          <label
            style={{
              color: "#000000",
              margin: 0,
              fontSize: "1rem",
              marginRight: "5px",
            }}
          >
            Don't have an account?
          </label>
          <label
            onClick={() => navigate("/register")}
            style={{
              color: "#000000",
              fontWeight: "bold",
              textDecoration: "underline",
              margin: 0,
              fontSize: "1rem",
              cursor: "pointer",
            }}
          >
            Sign up
          </label>
        </div>
        {/* or Sign in */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
          }}
        >
          <label
            style={{
              color: "#000000",
              margin: 0,
              fontSize: "1rem",
              marginRight: "5px",
            }}
          >
            or
          </label>
          <label
            onClick={() => navigate("/login")}
            style={{
              color: "#000000",
              fontWeight: "bold",
              textDecoration: "underline",
              margin: 0,
              fontSize: "1rem",
              cursor: "pointer",
            }}
          >
            Sign in
          </label>
        </div>
      </div>
    </div>
  );
}

export default Newpw;
