# Build stage
FROM azul/zulu-openjdk:17.0.9 AS build
WORKDIR /workspace/app

# Copy Gradle wrapper and build configuration
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Ensure Gradle wrapper is executable and cache dependencies
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -x test

# Copy source code and build application
COPY src src
RUN ./gradlew bootJar --no-daemon -x test && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*.jar)

# Run stage
FROM azul/zulu-openjdk:17.0.9-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency

# Copy only necessary files from the build stage
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Use a minimal entrypoint
ENTRYPOINT ["java","-cp","app:app/lib/*","org.example.backend.BackendApplication"]
