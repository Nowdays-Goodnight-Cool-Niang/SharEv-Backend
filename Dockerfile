FROM azul/zulu-openjdk:17.0.9 AS builder

WORKDIR /app

# BUILD_PROFILE 인자를 받도록 추가. 기본값은 prod로 설정하여 안전하게 배포용 빌드.
ARG BUILD_PROFILE=prod

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성만 먼저 다운로드하여 캐싱 활용
RUN ./gradlew dependencies --no-daemon

COPY src ./src

# Gradle 빌드 명령을 BUILD_PROFILE에 따라 조건부로 실행
RUN if [ "$BUILD_PROFILE" = "prod" ]; then \
    ./gradlew clean build -x test --no-daemon -Pprofile=prod; \
else \
    ./gradlew clean build --no-daemon -Pprofile=${BUILD_PROFILE}; \
fi

# 런타임 스테이지: 최종 이미지
FROM azul/zulu-openjdk:17.0.9

WORKDIR /app
# 빌더 스테이지에서 생성된 JAR 파일을 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE 환경 변수를 사용하여 프로필 동적 설정
# 기본값은 prod로 유지하되, compose.yaml에서 오버라이드 가능
ENV TZ=Asia/Seoul
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
