# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Image to run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/api-user-1.0-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]