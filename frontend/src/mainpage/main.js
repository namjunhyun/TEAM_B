import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./main.css";
import godown from "../img/godown.png";

function Main() {
  const navigate = useNavigate();

  // 상태 관리
  const [animate1, setAnimate1] = useState(false);
  const [activeButton, setActiveButton] = useState(null); // 초기값을 null로 변경
  const [showActionButtons, setShowActionButtons] = useState(false);
  const [summaryData, setSummaryData] = useState(null);
  const [currentSummary, setCurrentSummary] = useState(""); // 초기값을 빈 문자열로 변경
  const BoxRef = useRef();

  // 0.5초 후 노란 박스 애니메이션 시작
  useEffect(() => {
    const timer = setTimeout(() => {
      setAnimate1(true);
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  // 컴포넌트 마운트 시 저장된 요약 데이터 불러오기
  useEffect(() => {
    const savedData = localStorage.getItem("summaryData");
    if (savedData) {
      try {
        const parsedData = JSON.parse(savedData);
        setSummaryData(parsedData);
        // 기본값으로 간단요약 표시
        setCurrentSummary(
          parsedData.간단요약 ||
            parsedData.text ||
            "요약 데이터를 불러올 수 없습니다."
        );
      } catch (error) {
        console.error("데이터 파싱 오류:", error);
        setCurrentSummary("저장된 요약 데이터를 불러올 수 없습니다.");
      }
    } else {
      // 테스트용 기본 데이터
      const defaultData = {
        text: "회의 전체 텍스트 내용입니다...",
        간단요약:
          "재택근무가 확산되면서 워라밸 향상과 비용 절감 등의 이점이 있지만, 소통 부족과 조직 소속감 약화 등의 문제도 존재한다.",
        상세요약:
          "재택근무는 코로나19 팬데믹을 계기로 빠르게 확산된 근무 형태이다. 직원들은 출퇴근 시간이 사라지면서 더 많은 여유 시간을 확보할 수 있게 되었다. 이는 워라밸(Work-Life Balance) 향상에 긍정적인 영향을 주었다. 또한, 자율적인 시간 관리가 가능해져 개인의 집중력이 오히려 높아지기도 한다. 기업 입장에서는 사무실 운영비용 절감 등의 경제적 이점이 존재한다. 반면, 팀원 간의 소통이 부족해지며 협업 효율이 낮아지는 경우도 있다.",
        키워드요약:
          "• 재택근무, 코로나19 팬데믹\n• 워라밸 향상, 여유 시간 확보\n• 자율적 시간 관리, 집중력 향상\n• 사무실 운영비용 절감\n• 소통 부족, 협업 효율 저하\n• 조직 소속감 약화\n• 하이브리드 근무 형태",
        fileName: "sample_audio.mp3",
        uploadTime: new Date().toLocaleString(),
      };
      setSummaryData(defaultData);
      setCurrentSummary(defaultData.간단요약);
    }
  }, []);

  // 스크롤 함수
  const scrollToBottom = () => {
    if (BoxRef.current) {
      BoxRef.current.scrollTo({
        top: BoxRef.current.scrollHeight,
        behavior: "smooth",
      });
    }
  };

  // 버튼 클릭 핸들러
  const handleButtonClick = (buttonId, originalOnClick) => {
    setActiveButton(buttonId);

    // 요약 타입에 따라 표시할 내용 변경
    if (summaryData) {
      switch (buttonId) {
        case "simple":
        case "간단요약":
          setCurrentSummary(summaryData.간단요약 || summaryData.text);
          break;
        case "detailed":
        case "상세요약":
          setCurrentSummary(summaryData.상세요약 || summaryData.text);
          break;
        case "keyword":
        case "키워드요약":
          setCurrentSummary(summaryData.키워드요약 || summaryData.text);
          break;
        default:
          setCurrentSummary(summaryData.간단요약 || summaryData.text);
      }
    }

    if (originalOnClick) {
      originalOnClick();
    }
  };

  // exportButton 클릭 핸들러
  const handleExportButtonClick = () => {
    setShowActionButtons((prev) => !prev);
  };

  // 새 파일 업로드 핸들러
  const handleNewUpload = () => {
    // 기존 데이터 삭제
    localStorage.removeItem("summaryData");
    navigate("/upload");
  };

  // 요약 타입명 가져오기 함수
  const getSummaryTypeName = (buttonId) => {
    switch (buttonId) {
      case "간단요약":
        return "간단 요약";
      case "상세요약":
        return "상세 요약";
      case "키워드요약":
        return "키워드 요약";
      default:
        return "요약";
    }
  };

  // 전체 내용을 포함한 텍스트 생성 함수
  const getFullContent = () => {
    const summaryTypeName = getSummaryTypeName(activeButton);

    let content = `파일명: ${summaryData?.fileName || "알 수 없음"}\n`;
    content += `생성일시: ${
      summaryData?.uploadTime || new Date().toLocaleString()
    }\n\n`;

    // 원본 텍스트
    content += `=== 원본 텍스트 ===\n`;
    content += `${
      summaryData?.text || "원본 텍스트를 불러올 수 없습니다."
    }\n\n`;

    // 선택된 요약이 있는 경우에만 추가
    if (activeButton && currentSummary) {
      content += `=== ${summaryTypeName} ===\n`;
      content += `${currentSummary}`;
    }

    return content;
  };

  // 텍스트 복사 함수
  const copyToClipboard = async () => {
    try {
      const fullContent = getFullContent();
      await navigator.clipboard.writeText(fullContent);
      alert("원본 텍스트와 요약이 클립보드에 복사되었습니다.");
    } catch (err) {
      const textArea = document.createElement("textarea");
      textArea.value = getFullContent();
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
      alert("원본 텍스트와 요약이 클립보드에 복사되었습니다.");
    }
  };

  // 파일로 내보내기 함수
  const exportToFile = () => {
    const fileName = `요약_${
      summaryData?.fileName?.replace(/\.[^/.]+$/, "") || "audio"
    }_${new Date().toLocaleDateString("ko-KR").replace(/\./g, "")}.txt`;

    const fileContent = getFullContent();

    const blob = new Blob([fileContent], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  // 버튼 데이터 배열
  const summaryButtons = [
    {
      id: "간단요약",
      text: "간단 요약",
      title: "짧고 핵심적인 한두 문장으로 내용을 압축한 요약",
      onClick: () => console.log("간단 요약 클릭"),
    },
    {
      id: "상세요약",
      text: "상세 요약",
      title: "전체 내용을 자세히 풀어 설명한 장문 요약",
      onClick: () => console.log("상세 요약 클릭"),
    },
    {
      id: "키워드요약",
      text: "키워드 요약",
      title: "핵심 키워드만 뽑아낸 리스트형 요약",
      onClick: () => console.log("키워드 요약 클릭"),
    },
  ];

  // 액션 버튼 데이터 배열
  const actionButtons = [
    {
      id: "copy",
      text: "📄 텍스트 복사",
      title: "원본 텍스트와 선택된 요약을 클립보드에 복사합니다",
      onClick: copyToClipboard,
      style: {
        backgroundColor: "#F2C81B",
        color: "white",
        border: "none",
      },
    },
    {
      id: "export",
      text: "💾 파일로 내보내기",
      title: "원본 텍스트와 선택된 요약을 텍스트 파일로 다운로드합니다",
      onClick: exportToFile,
      style: {
        backgroundColor: "#F2C81B",
        color: "white",
        border: "none",
      },
    },
    {
      id: "newUpload",
      text: "📁 새 파일 업로드",
      title: "새로운 파일을 업로드합니다",
      onClick: handleNewUpload,
      style: {
        backgroundColor: "#00492C",
        color: "white",
        border: "none",
      },
    },
  ];

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
      {/* 제목 - 클릭하면 업로드 페이지로 이동 */}
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
          cursor: "pointer",
          transition: "all 0.3s ease-in-out",
        }}
        onClick={handleNewUpload}
        onMouseEnter={(e) => {
          e.target.style.opacity = "0.8";
        }}
        onMouseLeave={(e) => {
          e.target.style.opacity = "1";
        }}
        title="새 파일 업로드"
      >
        Saymary
      </h1>

      {/* 메인 컨텐츠 박스 */}
      <div
        className="custom-scroll"
        ref={BoxRef}
        style={{
          backgroundColor: "#FFFCE4",
          position: "absolute",
          borderRadius: "10px",
          boxShadow: "-20px 5px 0px rgba(0, 0, 0, 0.2)",
          top: "20%",
          left: "12%",
          width: "88%",
          height: "80%",
          opacity: animate1 ? 1 : 0,
          transition: "all 0.3s ease-in-out",
          overflowY: "auto",
          zIndex: 1000,
        }}
      >
        {/* 파일명 헤더 */}
        <h1
          style={{
            color: "#656247",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 500,
            fontSize: "18px",
            margin: "0px",
            paddingTop: "3%",
            paddingBottom: "5px",
            paddingLeft: "7%",
            position: "relative",
          }}
        >
          파일명: {summaryData?.fileName || "알 수 없음"}
          {/* exportButton */}
          <button
            className="exportButton"
            title="내보내기 옵션"
            onClick={handleExportButtonClick}
            style={{
              color: "#656247",
              backgroundColor: showActionButtons ? "#d4d1b8" : "#ecead5",
              fontFamily: "Noto Sans KR, sans-serif",
              fontWeight: 600,
              fontSize: "11px",
              border: "none",
              lineHeight: "0.1",
              justifyContent: "center",
              textAlign: "center",
              cursor: "pointer",
              padding: "10px 10px",
              marginLeft: "10px",
              borderRadius: "15px",
              transition: "all 0.2s ease-in-out",
              position: "relative",
            }}
          >
            . . .{/* 액션 버튼들 */}
            <div
              style={{
                position: "absolute",
                top: "100%",
                left: "0",
                zIndex: 1001,
                display: "flex",
                flexDirection: "column",
                gap: "8px",
                marginTop: "5px",
                opacity: showActionButtons ? 1 : 0,
                transform: showActionButtons
                  ? "translateY(0)"
                  : "translateY(-10px)",
                transition: "all 0.3s ease-in-out",
                visibility: showActionButtons ? "visible" : "hidden",
                pointerEvents: showActionButtons ? "auto" : "none",
              }}
            >
              {actionButtons.map((button) => (
                <button
                  key={button.id}
                  title={button.title}
                  aria-label={button.title}
                  onClick={button.onClick}
                  style={{
                    padding: "8px 16px",
                    borderRadius: "6px",
                    fontFamily: "Noto Sans KR, sans-serif",
                    fontWeight: 500,
                    fontSize: "0.7rem",
                    cursor: "pointer",
                    transition: "all 0.2s ease-in-out",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
                    border: "none",
                    whiteSpace: "nowrap",
                    minWidth: "140px",
                    ...button.style,
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.transform = "scale(1.02)";
                    e.target.style.boxShadow = "0 4px 12px rgba(0,0,0,0.25)";
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.transform = "scale(1)";
                    e.target.style.boxShadow = "0 2px 8px rgba(0,0,0,0.15)";
                  }}
                >
                  {button.text}
                </button>
              ))}
            </div>
          </button>
        </h1>

        {/* 날짜 표시 */}
        <h2
          style={{
            color: "#656247",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 300,
            fontSize: "11px",
            paddingLeft: "7%",
            paddingTop: "0px",
            margin: "0px",
          }}
        >
          {summaryData?.uploadTime || new Date().toLocaleString()}
        </h2>

        {/* 요약 텍스트 본문 - 원본 텍스트 표시 */}
        <p
          style={{
            color: "#656247",
            backgroundColor: "#ECEAD5",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 400,
            fontSize: "12px",
            marginTop: "20px",
            marginLeft: "7%",
            marginRight: "8%",
            paddingTop: "40px",
            paddingBottom: "50px",
            paddingLeft: "40px",
            paddingRight: "40px",
            lineHeight: "2",
            borderRadius: "10px",
            whiteSpace: "pre-line", // 줄바꿈 문자 처리
          }}
        >
          {summaryData?.text || "원본 텍스트를 불러올 수 없습니다."}

          {/* 요약 타입 선택 버튼들 */}
          <div
            style={{
              textAlign: "center",
              marginTop: "20px",
              display: "flex",
              justifyContent: "center",
              gap: "10%",
            }}
          >
            {summaryButtons.map((button) => (
              <button
                key={button.id}
                title={button.title}
                aria-label={`${button.text}: ${button.title}`}
                onClick={() => handleButtonClick(button.id, button.onClick)}
                style={{
                  padding: "10px 30px",
                  borderRadius: "10px",
                  border: `dashed 4px ${
                    activeButton === button.id ? "#F2C81B" : "#9a8018ff"
                  }`,
                  backgroundColor:
                    activeButton === button.id
                      ? "rgba(242, 200, 27, 0.1)"
                      : "transparent",
                  color: "#000000",
                  fontFamily: "Noto Sans KR, sans-serif",
                  fontWeight: 500,
                  fontSize: "0.7rem",
                  cursor: "pointer",
                  marginTop: "15px",
                  transition: "all 0.3s ease-in-out",
                }}
              >
                {button.text}
              </button>
            ))}
          </div>
        </p>

        {/* 선택된 요약본 표시 영역 */}
        <div
          style={{
            marginTop: "20px",
            marginLeft: "7%",
            marginRight: "8%",
            marginBottom: "50px",
          }}
        >
          <div
            style={{
              color: "#4a4332",
              backgroundColor: "#ECEAD5",
              fontFamily: "Noto Sans KR, sans-serif",
              fontWeight: 400,
              fontSize: "13px",
              padding: "30px",
              lineHeight: "2.2",
              borderRadius: "10px",
              whiteSpace: "pre-line",
              minHeight: "120px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              textAlign: activeButton ? "left" : "center",
            }}
          >
            {currentSummary || "위의 버튼을 클릭하여 요약을 확인해보세요! 📋"}
          </div>
        </div>

        {/* 하단 스크롤 버튼 */}
        <img
          src={godown}
          onClick={scrollToBottom}
          alt="최하단으로 이동"
          title="최하단으로 스크롤"
          style={{
            position: "fixed",
            left: "55%",
            bottom: "50px",
            transform: "translateX(-50%)",
            zIndex: 999,
            cursor: "pointer",
            width: "50px",
            height: "30px",
          }}
        />
      </div>
    </div>
  );
}

export default Main;
