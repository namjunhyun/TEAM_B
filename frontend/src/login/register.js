import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Register() {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [confirmPasswordError, setConfirmPasswordError] = useState("");

  // 이메일 입력 형식 제어
  const handleEmailChange = (e) => {
    const { value } = e.target;
    const filteredValue = value.replace(/[^a-zA-Z0-9@.!*$]/g, ""); // 이메일 input에 영어 대소문자, 숫자, @, ., !, *, $ 만 허용
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setEmail(filteredValue);
    if (filteredValue === "" || emailRegex.test(filteredValue)) {
      setEmailError(""); // 통과하면 에러 제거
    } else {
      setEmailError("Please enter a valid email address.");
    }
  };

  // 비밀번호 입력 형식 제어
  const handlePasswordChange = (e) => {
    const { value } = e.target;
    setPassword(value);
    if (value.length > 0 && value.length < 6) {
      setPasswordError("Password must be at least 6 characters long.");
    } else {
      setPasswordError("");
    }
    if (confirmPassword && value !== confirmPassword) {
      setConfirmPasswordError("Passwords do not match.");
    } else {
      setConfirmPasswordError("");
    }
  }; // 비밀번호 input에 최소 6자 이상 입력

  // 비밀번호 확인 입력
  const handleConfirmPasswordChange = (e) => {
    const { value } = e.target;
    setConfirmPassword(value);
    if (value !== password) {
      setConfirmPasswordError("Passwords do not match.");
    } else {
      setConfirmPasswordError("");
    }
  };
const handleLogin = async () => {
  try {
    const response = await fetch("http://localhost:8080/api/user/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        password: password,
        nickname: nickname,
      }),
    });

    const text = await response.text();
    console.log("응답 내용:", text);

    if (text.includes("성공")) {
      alert("회원 가입 성공!");
      // 임시 테스트용, 토큰 저장 로직 추가 필요
      navigate("/login");
    } else if (text.includes("이미")) {
      alert("이미 등록된 이메일입니다.");
    } else {
      alert("회원 가입 실패!");
    }
  } catch (error) {
    alert("서버 연결 실패!");
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
        Wecome :-)
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
        {/* Nickname 인풋 */}
        <h3
          style={{
            marginBottom: "5px",
            fontSize: "1.2rem",
            margin: "0 auto",
            marginLeft: "18%",
          }}
        >
          Nickname
        </h3>
        <input
          type="text"
          placeholder="Enter your nickname"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          style={{
            marginBottom: "5px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: "#FFFCE4",
            border: "solid 2px #C7C29B",
            padding: "10px",
            width: "300px",
            marginLeft: "18%",
          }}
        />

        {/* 이메일 인풋 */}
        <h3
          style={{
            marginBottom: "5px",
            marginTop: "10px",
            fontSize: "1.2rem",
            marginLeft: "18%",
          }}
        >
          Email ID
        </h3>
        <input
          type="email"
          value={email}
          placeholder="Enter your email"
          onChange={handleEmailChange}
          style={{
            marginBottom: "5px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: "#FFFCE4",
            border: "solid 2px #C7C29B",
            padding: "10px",
            width: "300px",
            marginLeft: "18%",
          }}
        />
        {emailError && (
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
            {emailError}
          </p>
        )}

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
            marginBottom: "5px",
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

        {/* pw 확인 인풋 */}
        <h3
          style={{
            marginBottom: "5px",
            marginTop: "10px",
            fontSize: "1.2rem",
            marginLeft: "18%",
            color: "#00492C",
          }}
        >
          Confirm Password
        </h3>
        <input
          type="password"
          placeholder="Enter your password"
          value={confirmPassword}
          onChange={handleConfirmPasswordChange}
          style={{
            marginBottom: "5px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: "#FFFCE4",
            border: "solid 2px #C7C29B",
            padding: "10px",
            width: "300px",
            marginLeft: "18%",
          }}
        />
        {confirmPasswordError && (
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
            {confirmPasswordError}
          </p>
        )}

        {/* sign up 버튼 */}
        <button
          onClick={handleLogin}
          style={{
            padding: "12px",
            fontSize: "1rem",
            borderRadius: "5px",
            backgroundColor: "#00492C",
            color: "white",
            border: "none",
            cursor: "pointer",
            marginLeft: "18%",
            marginTop: "10px",
            width: "320px",
          }}
        >
          Sign up
        </button>
      </div>
    </div>
  );
}

export default Register;
