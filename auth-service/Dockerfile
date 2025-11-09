# Multi-stage build for Auth Service
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /workspace

# Copy parent pom
COPY pom.xml ./

# Copy all modules (common, auth-service, course-service)
COPY common ./common
COPY auth-service ./auth-service
COPY course-service ./course-service

# Build the entire parent project (this builds common first, then all services)
RUN mvn clean package -DskipTests -pl auth-service -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthchecks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /workspace/auth-service/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:$PORT/auth/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.port=${PORT:-8081}", \
    "-jar", \
    "app.jar"]
