import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function UploadFile() {
  const navigate = useNavigate();
  const [selectedFile, setSelectedFile] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadError, setUploadError] = useState(null);

  // 상태 관리
  const [animate1, setAnimate1] = useState(false);

  // 0.5초 후 노란 박스 애니메이션 시작
  useEffect(() => {
    const timer = setTimeout(() => {
      setAnimate1(true);
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  // 상태 초기화 함수
  const resetUploadState = () => {
    setIsUploading(false);
    setSelectedFile(null);
    setUploadProgress(0);
    setUploadError(null);
  };

  // API 호출 함수
  const uploadFileToAPI = async (file) => {
    setIsUploading(true);
    setUploadProgress(0);
    setUploadError(null);

    let progressInterval;

    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("situation", "회의"); // 기본값으로 설정
      formData.append("audience", "일반"); // 기본값으로 설정
      formData.append("style", "친근"); // 기본값으로 설정

      // 진행률 시뮬레이션
      progressInterval = setInterval(() => {
        setUploadProgress((prev) => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 200);

      const response = await fetch("/api/fastapi/upload", {
        method: "POST",
        body: formData,
      });

      clearInterval(progressInterval);
      progressInterval = null;
      setUploadProgress(100);

      if (response.ok) {
        const result = await response.json();
        console.log("API 응답:", result);

        if (result.success) {
          // 성공 시 요약 데이터를 localStorage에 저장
          localStorage.setItem(
            "summaryData",
            JSON.stringify({
              text: result.text,
              summaries: result.summaries,
              간단요약: result["간단요약"],
              상세요약: result["상세요약"],
              키워드요약: result["키워드요약"],
              fileName: file.name,
              uploadTime: new Date().toLocaleString(),
            })
          );

          setTimeout(() => {
            navigate("/main");
          }, 1000);
        } else {
          throw new Error(result.message || "업로드에 실패했습니다.");
        }
      } else {
        throw new Error(`서버 오류: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      console.error("업로드 오류:", error);

      // 진행률 인터벌 정리
      if (progressInterval) {
        clearInterval(progressInterval);
      }

      // 에러 상태 설정
      setUploadError(error.message);

      // 사용자에게 알림
      alert(`업로드 실패: ${error.message}`);

      // 2초 후 상태 초기화 (사용자가 에러 메시지를 볼 시간을 줌)
      setTimeout(() => {
        resetUploadState();
      }, 2000);
    }
  };

  // 파일 업로드 처리
  const handleFileUpload = (file) => {
    const token = localStorage.getItem("accessToken");

    if (!token) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    if (file && file.type.startsWith("audio/")) {
      setSelectedFile(file);
      console.log("업로드된 파일:", file);

      // API 호출
      uploadFileToAPI(file);
    } else {
      alert("음성 파일만 업로드 가능합니다.");
      resetUploadState();
    }
  };

  // 파일 선택 핸들러
  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    if (file) {
      handleFileUpload(file);
    }
    // input 값 초기화 (같은 파일을 다시 선택할 수 있도록)
    event.target.value = "";
  };

  // 드래그 앤 드롭 핸들러
  const handleDragOver = (event) => {
    event.preventDefault();
    if (!isUploading) {
      setIsDragging(true);
    }
  };

  const handleDragLeave = (event) => {
    event.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setIsDragging(false);

    if (!isUploading) {
      const file = event.dataTransfer.files[0];
      if (file) {
        handleFileUpload(file);
      }
    }
  };

  // 파일 입력 클릭
  const handleClick = () => {
    if (!isUploading) {
      document.getElementById("fileInput").click();
    }
  };

  // 업로드 취소 함수
  const handleCancelUpload = () => {
    if (isUploading) {
      resetUploadState();
    }
  };

  // 업로드 상태에 따른 텍스트 결정
  const getStatusText = () => {
    if (uploadError) {
      return "업로드 실패 - 다시 시도해주세요";
    }
    if (isUploading) {
      if (uploadProgress < 30) return "파일 업로드 중...";
      if (uploadProgress < 60) return "음성 인식 중...";
      if (uploadProgress < 90) return "요약 생성 중...";
      return "완료 중...";
    }
    if (selectedFile && !isUploading)
      return `선택된 파일: ${selectedFile.name}`;
    return "음성파일을 업로드 해주세요...";
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
          cursor: isUploading ? "default" : "pointer",
        }}
        onClick={!isUploading ? () => navigate("/") : undefined}
        title={isUploading ? "" : "홈으로 돌아가기"}
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
          opacity: animate1 ? 1 : 0,
          transition: "all 0.3s ease-in-out",
          overflowY: "auto",
          overflowX: "hidden",
        }}
      >
        {/* 업로드 박스 */}
        <div
          style={{
            color: "#656247",
            backgroundColor: isDragging
              ? "#ddd8be"
              : uploadError
              ? "#f5e6e6"
              : isUploading
              ? "#f0edd8"
              : "#ECEAD5",
            fontFamily: "Noto Sans KR, sans-serif",
            fontWeight: 600,
            fontSize: "1.5rem",
            position: "relative",
            top: "50%",
            transform: "translateY(-50%)",
            marginLeft: "20%",
            marginRight: "20%",
            paddingTop: "60px",
            paddingBottom: "60px",
            paddingLeft: "3%",
            paddingRight: "3%",
            borderRadius: "10px",
            border: isDragging
              ? "2px dashed #F2C81B"
              : uploadError
              ? "2px solid #e74c3c"
              : isUploading
              ? "2px solid #F2C81B"
              : "2px dashed transparent",
            transition: "all 0.3s ease-in-out",
            cursor: isUploading ? "default" : "pointer",
          }}
          onDragOver={!isUploading ? handleDragOver : undefined}
          onDragLeave={!isUploading ? handleDragLeave : undefined}
          onDrop={!isUploading ? handleDrop : undefined}
          onClick={!isUploading ? handleClick : undefined}
        >
          <div style={{ textAlign: "center" }}>
            <p
              style={{
                margin: "0px",
                fontSize: isUploading ? "1.2rem" : "1.5rem",
                color: uploadError ? "#e74c3c" : "#656247",
              }}
            >
              {getStatusText()}
            </p>

            {/* 업로드 진행률 표시 */}
            {isUploading && !uploadError && (
              <div style={{ margin: "20px 0" }}>
                <div
                  style={{
                    width: "80%",
                    height: "8px",
                    backgroundColor: "#d4d1b8",
                    borderRadius: "4px",
                    margin: "0 auto",
                    overflow: "hidden",
                  }}
                >
                  <div
                    style={{
                      width: `${uploadProgress}%`,
                      height: "100%",
                      backgroundColor: "#F2C81B",
                      borderRadius: "4px",
                      transition: "width 0.3s ease-in-out",
                    }}
                  />
                </div>
                <p
                  style={{
                    fontSize: "0.9rem",
                    margin: "10px 0 0 0",
                    color: "#8a7d5c",
                  }}
                >
                  {uploadProgress}%
                </p>

                {/* 취소 버튼 */}
                <button
                  onClick={handleCancelUpload}
                  style={{
                    padding: "5px 15px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#F2C81B",
                    color: "#ffffff",
                    fontFamily: "Noto Sans KR, sans-serif",
                    fontWeight: 500,
                    fontSize: "0.6rem",
                    cursor: "pointer",
                    marginTop: "10px",
                    transition: "all 0.3s ease-in-out",
                  }}
                >
                  취소
                </button>
              </div>
            )}

            {!isUploading && !uploadError && (
              <>
                <p
                  style={{
                    fontFamily: "Noto Sans KR, sans-serif",
                    fontWeight: 600,
                    fontSize: "1rem",
                    margin: "10px 0",
                  }}
                >
                  {isDragging ? "파일을 여기에 놓으세요" : "Drag & Drop"}
                </p>
                <button
                  style={{
                    padding: "10px 30px",
                    borderRadius: "10px",
                    border: "none",
                    backgroundColor:
                      selectedFile && !isUploading ? "#F2C81B" : "#00492C",
                    color: "#ffffff",
                    fontFamily: "Noto Sans KR, sans-serif",
                    fontWeight: 500,
                    fontSize: "0.7rem",
                    cursor: "pointer",
                    marginTop: "15px",
                    transition: "all 0.3s ease-in-out",
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleClick();
                  }}
                >
                  Click to upload file
                </button>
              </>
            )}

            {/* 에러 시 다시 시도 버튼 */}
            {uploadError && (
              <button
                onClick={resetUploadState}
                style={{
                  padding: "10px 30px",
                  borderRadius: "10px",
                  border: "none",
                  backgroundColor: "#F2C81B",
                  color: "#ffffff",
                  fontFamily: "Noto Sans KR, sans-serif",
                  fontWeight: 500,
                  fontSize: "0.7rem",
                  cursor: "pointer",
                  marginTop: "15px",
                  transition: "all 0.3s ease-in-out",
                }}
              >
                다시 시도
              </button>
            )}
          </div>

          {/* 숨겨진 파일 입력 */}
          <input
            id="fileInput"
            type="file"
            accept="audio/*"
            style={{ display: "none" }}
            onChange={handleFileSelect}
            disabled={isUploading}
          />
        </div>
      </div>
    </div>
  );
}

export default UploadFile;
