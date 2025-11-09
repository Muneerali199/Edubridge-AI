# Auth Service

Authentication and authorization service for EduBridge platform.

## Features

- ✅ User registration with email/phone
- ✅ Login with JWT token generation
- ✅ Refresh token mechanism
- ✅ Password hashing with BCrypt
- ✅ Role-based access control (RBAC)
- ✅ Spring Security integration
- ✅ OpenAPI/Swagger documentation
- ✅ Comprehensive unit tests

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| POST | `/api/auth/refresh` | Refresh access token |
| GET | `/api/auth/health` | Health check |

### Swagger UI

Access API documentation at: `http://localhost:8081/api/auth/swagger-ui.html`

## Quick Start

### 1. Start Infrastructure

```bash
# From project root
docker-compose up -d
```

### 2. Build the Service

```bash
cd auth-service
mvn clean install
```

### 3. Run the Service

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8081`

## Configuration

Configure via `application.yml` or environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/edubridge
    username: edubridge
    password: edubridge_dev_password

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours
  refresh-expiration: 604800000  # 7 days
```

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify -P integration-tests
```

### Test Coverage

```bash
mvn test jacoco:report
# Report will be in target/site/jacoco/index.html
```

## Usage Examples

### Register User

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

Response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000,
    "tokenType": "Bearer",
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john@example.com",
      "role": "STUDENT",
      "isVerified": false
    }
  }
}
```

### Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Refresh Token

```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

## Database Schema

The auth service uses the `auth` schema in PostgreSQL:

### Tables

#### users
- `id` (UUID, PK)
- `email` (VARCHAR, UNIQUE)
- `phone` (VARCHAR)
- `password_hash` (VARCHAR)
- `name` (VARCHAR)
- `role` (VARCHAR)
- `is_active` (BOOLEAN)
- `is_verified` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `last_login_at` (TIMESTAMP)
- `profile_json` (JSONB)

#### refresh_tokens
- `id` (UUID, PK)
- `user_id` (UUID, FK)
- `token` (VARCHAR)
- `expires_at` (TIMESTAMP)
- `created_at` (TIMESTAMP)
- `revoked` (BOOLEAN)

See migrations in `src/main/resources/db/migration/`

## Security

- Passwords are hashed using BCrypt (strength: 12)
- JWT tokens signed with HS256 algorithm
- CORS configured for allowed origins
- Stateless session management
- All endpoints except registration/login require authentication

## Error Handling

All errors return a consistent format:

```json
{
  "success": false,
  "message": "Error message",
  "error": {
    "code": "ERROR_CODE",
    "message": "Detailed error message",
    "field": "fieldName" // for validation errors
  },
  "timestamp": "2025-11-09T10:30:00Z"
}
```

Common error codes:
- `USER_ALREADY_EXISTS` (409)
- `INVALID_CREDENTIALS` (401)
- `ACCOUNT_DEACTIVATED` (403)
- `INVALID_TOKEN` (401)
- `VALIDATION_ERROR` (400)

## Development

### Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/edubridge/auth/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── repository/       # Database repositories
│   │   │   ├── service/          # Business logic
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── db/migration/     # Flyway migrations
│   │       └── application.yml   # Configuration
│   └── test/
│       └── java/                 # Unit tests
└── pom.xml
```

### Adding New Features

1. Create entity in `entity/` package
2. Create repository interface in `repository/`
3. Add Flyway migration in `db/migration/`
4. Implement service logic in `service/`
5. Create REST controller in `controller/`
6. Write unit tests
7. Update OpenAPI spec

## Troubleshooting

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check database
docker exec -it edubridge-postgres psql -U edubridge -d edubridge -c "\dt auth.*"
```

### JWT Issues

- Ensure `jwt.secret` is at least 256 bits
- Check token expiration times
- Verify token format in Authorization header: `Bearer <token>`

### Port Already in Use

```bash
# Kill process on port 8081
# Windows PowerShell:
Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process
```

## Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

## License

Copyright © 2025 EduBridge. All rights reserved.
