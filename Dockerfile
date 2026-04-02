FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
COPY --from=builder /workspace/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
