# Multi-stage build for Course Service
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy parent pom first
COPY ../pom.xml ./pom.xml
COPY ../common ./common

# Copy course service
COPY pom.xml ./course-service/pom.xml
COPY src ./course-service/src

# Build
WORKDIR /app/course-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthchecks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /app/course-service/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
