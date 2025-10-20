# --- Stage 1: Build app ---
    FROM gradle:8.8-jdk22 AS builder
    WORKDIR /app
    COPY . .
    RUN gradle clean build -x test --no-daemon
    
    # --- Stage 2: Run app ---
    FROM openjdk:22-jdk-slim
    WORKDIR /app
    
    # Copy JAR từ stage build
    COPY --from=builder /app/build/libs/*.jar app.jar
    
    EXPOSE 8080
    
    ENTRYPOINT ["java", "-jar", "app.jar"]
    