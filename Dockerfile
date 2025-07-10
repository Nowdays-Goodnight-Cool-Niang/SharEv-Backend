FROM azul/zulu-openjdk:17.0.9 AS builder

# 작업 디렉터리 설정
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle

# build.gradle 복사
COPY build.gradle ./

# 의존성 다운로드 (소스 코드 복사 전에 실행하여 캐싱 효율 증대)
RUN ./gradlew dependencies

# 소스 코드 복사
COPY src ./src

# JAR 파일 빌드
RUN ./gradlew clean build -x test --no-daemon

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

FROM azul/zulu-openjdk:17.0.9

# JAR 파일을 컨테이너의 /app 디렉터리로 복사
WORKDIR /app
COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
