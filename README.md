# EduBridge AI Platform

> AI-powered education platform delivering personalized learning at scale

## Overview

EduBridge is a Java-first, production-ready AI-powered education platform that provides:
- Personalized learning paths using AI/ML
- Adaptive assessments and skill tracking
- AI tutor for doubt solving and explanations
- Teacher workflows and analytics
- Offline-first support for low-connectivity areas

## Architecture

This is a **modular monolith** that can be split into microservices as needed:
- **auth-service**: Authentication, authorization, JWT management
- **user-service**: User profiles, enrollments, preferences
- **content-service**: Courses, lessons, curriculum management
- **assessment-service**: Adaptive quizzes, grading, skill tracking
- **ai-orchestrator-service**: LLM integration, prompt management, caching
- **analytics-service**: Progress tracking, KPIs, dashboards
- **payments-service**: Subscriptions, invoices, payment gateways
- **common**: Shared DTOs, utilities, configurations

## Tech Stack

- **Java 17+** with Spring Boot 3.x
- **Spring Security** + OAuth2/JWT
- **PostgreSQL** for relational data
- **Redis** for caching and sessions
- **MinIO/S3** for object storage
- **OpenAI/Gemini** for LLM capabilities
- **Docker** for containerization
- **JUnit 5** + Mockito for testing

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Development Setup

1. **Start infrastructure services:**
   ```bash
   docker-compose up -d
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

4. **Access the application:**
   - API Gateway: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## Project Structure

```
edubridge/
├── common/                 # Shared utilities and DTOs
├── auth-service/          # Authentication & authorization
├── user-service/          # User management
├── content-service/       # Course & content management
├── assessment-service/    # Quizzes & assessments
├── ai-orchestrator/       # AI/LLM integration
├── analytics-service/     # Analytics & reporting
├── payments-service/      # Payment processing
├── api-gateway/           # API Gateway
├── docker/                # Docker configurations
├── docs/                  # API documentation
└── scripts/               # Utility scripts
```

## Documentation

- **[Getting Started Guide](./GETTING_STARTED.md)** - Complete setup instructions and next steps
- **[Architecture Overview](./docs/ARCHITECTURE.md)** - System architecture and component design
- **[Development Roadmap](./ROADMAP.md)** - Detailed 12-week MVP plan
- **[API Specification](./docs/api/openapi.yml)** - OpenAPI 3.0 specification for all endpoints
- **[Contributing Guidelines](./CONTRIBUTING.md)** - How to contribute to the project

## API Documentation

OpenAPI specifications are available in the `docs/api` directory.

## Testing

```bash
# Run all tests
mvn test

# Run integration tests
mvn verify -P integration-tests

# Run with coverage
mvn test jacoco:report
```

## Security

- All passwords are hashed using BCrypt
- JWT tokens with configurable expiration
- Role-based access control (RBAC)
- TLS encryption in transit
- AES-256 encryption for sensitive data at rest

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

## License

Copyright © 2025 EduBridge. All rights reserved.
