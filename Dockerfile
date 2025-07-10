FROM azul/zulu-openjdk:17.0.9 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle ./
COPY src ./src

# 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 (Docker 내부에서 JAR 파일 생성)
RUN ./gradlew clean build -x test --no-daemon

# 런타임 스테이지: 최종 이미지
FROM azul/zulu-openjdk:17.0.9

WORKDIR /app
# 빌더 스테이지에서 생성된 JAR 파일을 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
