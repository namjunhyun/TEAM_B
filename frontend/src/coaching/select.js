import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";

function Select() {
  const navigate = useNavigate();
  
  const situation1 = [
    "Lecture (강의)",
    "Interview (면접)",
    "Presentation (발표)",
    "Speech (연설)",
    "Briefing (브리핑)",
  ];
  const situation2 = [
    "Professor / Teacher (교수 / 선생님)",
    "Interviewer (면접관)",
    "Colleague / Team member (동료 / 팀원)",
    "Client / Boss (고객 / 상사)",
    "General audience (일반 청중)",
  ];
  const situation3 = [
    "Explanatory (설명형)",
    "Self-introductory (자기소개형)",
    "Persuasive (설득형)",
    "Informal (비격식형)",
    "Formal (격식형)",
    "Q&A style (질문응답형)",
  ];

  const [activeStates1, setActiveStates1] = useState(null);
  const [activeStates2, setActiveStates2] = useState(null);
  const [activeStates3, setActiveStates3] = useState(null);

  const [selectedFile, setSelectedFile] = useState(null);
  const fileInputRef = useRef(null);

  const [isLoading, setIsLoading] = useState(false);

  const selectedSituation = situation1[activeStates1];
  const selectedAudience = situation2[activeStates2];
  const selectedStyle = situation3[activeStates3];

  const isReadyToUpload =
    activeStates1 !== null && activeStates2 !== null && activeStates3 !== null;

  const handleClick1 = (index) => {
    setActiveStates1((prev) => (prev === index ? null : index));
  };

  const handleClick2 = (index) => {
    setActiveStates2((prev) => (prev === index ? null : index));
  };

  const handleClick3 = (index) => {
    setActiveStates3((prev) => (prev === index ? null : index));
  };

  const handleButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click(); // input 클릭 트리거
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      handleUpload(file);
    }
    fileInputRef.current.value = null;
  };

  const handleUpload = async (file) => {
    if (!file) {
      alert("파일을 선택해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("situation", selectedSituation);
    formData.append("audience", selectedAudience);
    formData.append("style", selectedStyle);
    formData.append("file", file);

    setIsLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/coaching/feedback", {
        method: "POST",
        body: formData,
      });

      const result = await res.json();
      console.log("업로드 결과:", result);

      // 성공 시 Coach 페이지로 이동하면서 데이터 전달
      navigate("/coaching/result", {
        state: {
          feedback: result.feedback,
          situation: selectedSituation,
          audience: selectedAudience,
          style: selectedStyle,
          fileName: file.name,
          uploadTime: new Date().toLocaleString(),
        },
      });
    } catch (err) {
      console.error("업로드 중 오류:", err);
      alert("업로드 실패");
    } finally {
      setIsLoading(false);
    }
  };

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
      <h1
        style={{
          color: "#F2C81B",
          fontFamily: "Cormorant Garamond, serif",
          fontWeight: 600,
          fontSize: "60px",
          position: "absolute",
          padding: "0px",
          margin: "0px",
          top: "7%",
          left: "13%",
          opacity: 1,
        }}
      >
        Saymary
      </h1>
      {/* 노란박스 */}
      <div
        className="custom-scroll"
        style={{
          backgroundColor: "#FFFCE4",
          position: "absolute",
          borderRadius: "10px",
          boxShadow: "-20px 5px 0px rgba(0, 0, 0, 0.2)",
          top: "20%",
          left: "12%",
          width: "88%",
          height: "80%",
          overflowY: "auto", // 스크롤 가능, 스크롤바는 index.css에서 설정
          overflowX: "hidden",
        }}
      >
        {/* Situation */}
        <h1
          style={{
            color: "#000000",
            marginTop: "0.3%",
            marginBottom: "0px",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 500,
            fontSize: "2rem",
            paddingTop: "3%",
            paddingBottom: "5px",
            paddingLeft: "7%",
          }}
        >
          Situation
          <div>
            {situation1.map((situation1, idx) => (
              <button
                key={idx}
                onClick={() => handleClick1(idx)}
                style={{
                  display: "inline-block",
                  marginTop: "0.3%",
                  marginRight: "1%",
                  paddingLeft: "20px",
                  paddingRight: "20px",
                  paddingTop: "3px",
                  paddingBottom: "4px",
                  borderRadius: "10px",
                  cursor: "pointer",
                  fontFamily: "Noto Sans KR, sans-serif",
                  fontWeight: 500,
                  fontSize: "1.1rem",
                  lineHeight: "normal",
                  border: "1px solid #C7C29B",
                  backgroundColor:
                    activeStates1 === idx ? "#00492C" : "#ECEAD5",
                  color: activeStates1 === idx ? "#ffffff" : "#000000",
                }}
              >
                {situation1}
              </button>
            ))}
          </div>
        </h1>
        {/* Audience type */}
        <h1
          style={{
            color: "#000000",
            marginTop: "2%",
            marginBottom: "0px",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 500,
            fontSize: "2rem",
            paddingBottom: "5px",
            paddingLeft: "7%",
          }}
        >
          Audience type
          <div>
            {situation2.map((situation2, idx) => (
              <button
                key={idx}
                onClick={() => handleClick2(idx)}
                style={{
                  display: "inline-block",
                  marginTop: "0.3%",
                  marginRight: "1%",
                  paddingLeft: "20px",
                  paddingRight: "20px",
                  paddingTop: "3px",
                  paddingBottom: "4px",
                  borderRadius: "10px",
                  cursor: "pointer",
                  fontFamily: "Noto Sans KR, sans-serif",
                  fontWeight: 500,
                  fontSize: "1.1rem",
                  lineHeight: "normal",
                  border: "1px solid #C7C29B",
                  backgroundColor:
                    activeStates2 === idx ? "#00492C" : "#ECEAD5",
                  color: activeStates2 === idx ? "#ffffff" : "#000000",
                }}
              >
                {situation2}
              </button>
            ))}
          </div>
        </h1>
        {/* Speech style */}
        <h1
          style={{
            color: "#000000",
            marginTop: "2%",
            marginBottom: "2%",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 500,
            fontSize: "2rem",
            paddingBottom: "5px",
            paddingLeft: "7%",
          }}
        >
          Speech style
          <div>
            {situation3.map((situation3, idx) => (
              <button
                key={idx}
                onClick={() => handleClick3(idx)}
                style={{
                  display: "inline-block",
                  marginTop: "0.3%",
                  marginBottom: "0.3%",
                  marginRight: "1%",
                  paddingLeft: "20px",
                  paddingRight: "20px",
                  paddingTop: "3px",
                  paddingBottom: "4px",
                  borderRadius: "10px",
                  cursor: "pointer",
                  fontFamily: "Noto Sans KR, sans-serif",
                  fontWeight: 500,
                  fontSize: "1.1rem",
                  lineHeight: "normal",
                  border: "1px solid #C7C29B",
                  backgroundColor:
                    activeStates3 === idx ? "#00492C" : "#ECEAD5",
                  color: activeStates3 === idx ? "#ffffff" : "#000000",
                }}
              >
                {situation3}
              </button>
            ))}
          </div>
        </h1>
        <input
          type="file"
          accept=".mp3, .aac, .ac3, .ogg, .flac, .wav, .m4a"
          style={{ display: "none" }}
          ref={fileInputRef}
          onChange={handleFileChange}
        />
        {isReadyToUpload && (
          <button
            onClick={handleButtonClick}
            disabled={isLoading}
            style={{
              position: "fixed",
              top: "25%",
              right: "20px",
              zIndex: 1000,
              padding: "20px 60px",
              borderRadius: "20px",
              border: "none",
              backgroundColor: "#00492C",
              color: "#ffffff",
              fontFamily: "Noto Sans KR, sans-serif",
              fontWeight: 500,
              fontSize: "1.1rem",
              cursor: isLoading ? "wait" : "pointer",
              opacity: isLoading ? 0.6 : 1,
            }}
          >
            {isLoading ? "Uploading..." : "Upload File"}
          </button>
        )}
      </div>
    </div>
  );
}

export default Select;
