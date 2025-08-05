import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [rememberMe, setRememberMe] = useState(false); // 로그인 상태 관리

  useEffect(() => {
    const savedEmail = localStorage.getItem("rememberedEmail");
    if (savedEmail) {
      setEmail(savedEmail);
      setRememberMe(true);
    }
  }, []);

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

  const handlePasswordChange = (e) => {
    const { value } = e.target;
    setPassword(value);
    if (value.length > 0 && value.length < 6) {
      setPasswordError("Password must be at least 6 characters long.");
    } else {
      setPasswordError("");
    }
  }; // 비밀번호 input에 최소 6자 이상 입력

  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/user/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      const result = await response.json();
      console.log("응답 내용:", result);

      if (result.success) {
        alert("로그인 성공!");

        // accessToken 저장
        localStorage.setItem("accessToken", result.token);

        // remember me 저장
        if (rememberMe) {
          localStorage.setItem("rememberedEmail", email);
        } else {
          localStorage.removeItem("rememberedEmail");
        }

        navigate("/main");
      } else {
        alert(result.message); // 실패 메시지
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
        Please Login.
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
        {/* 이메일 인풋 */}
        <h3
          style={{
            marginBottom: "5px",
            fontSize: "1.2rem",
            marginLeft: "18%",
          }}
        >
          Email ID
        </h3>
        <input
          type="email"
          placeholder="Enter your email"
          value={email}
          onChange={handleEmailChange}
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

        {/* remember me 체크박스 & forgot password 링크 */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "20px",
            fontSize: "0.9rem",
          }}
        >
          <div
            style={{ display: "flex", alignItems: "center", marginLeft: "18%" }}
          >
            {/* remember me 체크박스 */}
            <input
              type="checkbox"
              id="remember-me"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
              style={{ marginRight: "5px" }}
            />
            <label
              htmlFor="remember-me"
              style={{
                textDecoration: "underline",
                margin: 0,
                fontSize: "1rem",
                cursor: "pointer",
              }}
            >
              Remember me
            </label>
          </div>

          {/* forgot password 링크 */}
          <label
            onClick={() => navigate("/forgot")}
            style={{
              textDecoration: "underline",
              margin: 0,
              fontSize: "1rem",
              cursor: "pointer",
              marginRight: "18%",
            }}
          >
            Forgot password
          </label>
        </div>
        {/* 로그인 버튼 */}
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
          Sign in
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
      </div>
    </div>
  );
}

export default Login;
