# EduBridge Quick Start Script
# This script helps you get started with EduBridge development

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   EduBridge AI Platform - Quick Start Setup" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Host "Checking prerequisites..." -ForegroundColor Yellow

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -replace '.*version "(.+?)".*','$1' }
    Write-Host "✓ Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java 17+ is required but not found" -ForegroundColor Red
    Write-Host "  Please install Java 17+ from https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Check Maven
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    Write-Host "✓ Maven found: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven is required but not found" -ForegroundColor Red
    Write-Host "  Please install Maven from https://maven.apache.org/" -ForegroundColor Yellow
    exit 1
}

# Check Docker
try {
    $dockerVersion = docker --version
    Write-Host "✓ Docker found: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker is required but not found" -ForegroundColor Red
    Write-Host "  Please install Docker from https://www.docker.com/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Starting infrastructure services..." -ForegroundColor Yellow

# Start Docker Compose
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Infrastructure services started successfully" -ForegroundColor Green
} else {
    Write-Host "✗ Failed to start infrastructure services" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Waiting for services to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check PostgreSQL
Write-Host "Checking PostgreSQL..." -ForegroundColor Yellow
$pgReady = docker exec edubridge-postgres pg_isready -U edubridge 2>&1
if ($pgReady -like "*accepting connections*") {
    Write-Host "✓ PostgreSQL is ready" -ForegroundColor Green
} else {
    Write-Host "⚠ PostgreSQL might not be ready yet" -ForegroundColor Yellow
}

# Check Redis
Write-Host "Checking Redis..." -ForegroundColor Yellow
$redisReady = docker exec edubridge-redis redis-cli -a edubridge_redis_password ping 2>&1
if ($redisReady -like "*PONG*") {
    Write-Host "✓ Redis is ready" -ForegroundColor Green
} else {
    Write-Host "⚠ Redis might not be ready yet" -ForegroundColor Yellow
}

# Check MinIO
Write-Host "Checking MinIO..." -ForegroundColor Yellow
try {
    $minioReady = Invoke-WebRequest -Uri "http://localhost:9000/minio/health/live" -UseBasicParsing -ErrorAction SilentlyContinue
    Write-Host "✓ MinIO is ready" -ForegroundColor Green
} catch {
    Write-Host "⚠ MinIO might not be ready yet" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Building the project..." -ForegroundColor Yellow

# Build the project
mvn clean install -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Project built successfully" -ForegroundColor Green
} else {
    Write-Host "✗ Build failed" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   Setup Complete!" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Your EduBridge development environment is ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Services running:" -ForegroundColor Yellow
Write-Host "  • PostgreSQL: localhost:5432" -ForegroundColor White
Write-Host "  • Redis: localhost:6379" -ForegroundColor White
Write-Host "  • MinIO Console: http://localhost:9001" -ForegroundColor White
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Run a service:" -ForegroundColor White
Write-Host "     cd auth-service" -ForegroundColor Cyan
Write-Host "     mvn spring-boot:run" -ForegroundColor Cyan
Write-Host ""
Write-Host "  2. Access API documentation:" -ForegroundColor White
Write-Host "     http://localhost:8081/api/auth/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "  3. View logs:" -ForegroundColor White
Write-Host "     docker-compose logs -f" -ForegroundColor Cyan
Write-Host ""
Write-Host "  4. Stop infrastructure:" -ForegroundColor White
Write-Host "     docker-compose down" -ForegroundColor Cyan
Write-Host ""
Write-Host "Happy coding! 🚀" -ForegroundColor Green
