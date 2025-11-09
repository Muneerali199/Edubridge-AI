# Multi-stage build for Course Service
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

# Copy parent pom and common module
COPY pom.xml ./
COPY common ./common

# Copy course service
COPY course-service/pom.xml ./course-service/
COPY course-service/src ./course-service/src

# Build the application
RUN mvn -f course-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthchecks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /workspace/course-service/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:$PORT/api/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.port=${PORT:-8082}", \
    "-jar", \
    "app.jar"]
