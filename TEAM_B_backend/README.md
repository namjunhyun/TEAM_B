# 디렉토리 #
### 처음 생각난대로 일단 적어봤습니다 ###

<pre><code>```
TEAM_B_backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/TEAM_B_backend/
│   │   │       ├── TEAM_B_backendApplication.java  # 메인 클래스
│   │   │       ├── controller/                  # 요청 받는 컨트롤러
│   │   │       │   └── TranscriptionController.java
│   │   │       ├── service/                     # 비즈니스 로직
│   │   │       │   └── TranscriptionService.java
│   │   │       ├── model/                       # DTO/Entity 정의
│   │   │       │   ├── Transcript.java
│   │   │       │   └── TranscriptRequestDto.java
│   │   │       ├── repository/                  # JPA Repository
│   │   │       │   └── TranscriptRepository.java
│   │   │       └── config/                      # 설정 (CORS, Bean 등)
│   │   │           └── WebConfig.java
│   │   └── resources/
│   │       ├── application.yml                  # 환경 설정
│   │       ├── static/                          # 정적 자원
│   │       └── templates/                       # Thymeleaf 템플릿 사용 시
│
├── src/test/java/                               # 테스트 코드
│   └── com/example/TEAM_B_backend/
│       └── TEAM_B_backendApplicationTest.java
│
├── build.gradle 또는 pom.xml                    # 의존성 설정
├── README.md
└── .gitignore
└── docker-compose.yml
└── Dockerfile
```</code></pre>

# 남준현 


