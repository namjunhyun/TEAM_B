FROM openjdk:17-jdk-slim
# 자바 17, 최소 슬림 이미지 (좋음)

RUN apt-get update && apt-get install -y dos2unix
# dos2unix: 윈도우에서 복사한 gradlew 스크립트 개행문자 수정용 (유지해도 좋음)

WORKDIR /app
# 작업 디렉토리 설정

COPY gradlew ./
# gradlew (gradle 실행 스크립트) 복사
COPY gradle gradle
# gradle wrapper 폴더 복사
COPY build.gradle ./
# gradle 설정 파일 복사
COPY settings.gradle ./
# settings.gradle도 필요
RUN dos2unix gradlew
# 개행문자 수정

COPY src src
# 소스코드 복사

RUN ./gradlew build --no-daemon
# 빌드. 단, 이 단계에서 `application.yml`이 없으면 실패할 수도 있음

EXPOSE 8080
# Spring Boot 기본 포트

ENTRYPOINT ["java", "-jar", "build/libs/summer_project-0.0.1-SNAPSHOT.jar"]
# 빌드 결과 jar 파일을 실행
