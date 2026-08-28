# ===== STAGE 1: Spring Boot 빌드 스테이지 =====
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /demo
COPY build.gradle settings.gradle /demo/
COPY gradle /demo/gradle
COPY gradlew /demo/
COPY src /demo/src
RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon

# ===== STAGE 2: 실행 스테이지 =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /demo
COPY --from=builder /demo/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]