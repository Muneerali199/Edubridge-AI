# Multi-stage build for Auth Service
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy parent pom first
COPY ../pom.xml ./pom.xml
COPY ../common ./common

# Copy auth service
COPY pom.xml ./auth-service/pom.xml
COPY src ./auth-service/src

# Build
WORKDIR /app/auth-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthchecks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /app/auth-service/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
