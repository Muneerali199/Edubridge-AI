# EduBridge Quick Start Script
# This script starts all required services

Write-Host "🎓 EduBridge AI Platform - Quick Start" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "auth-service")) {
    Write-Host "❌ Error: Please run this script from the Edubridge root directory" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Checking prerequisites..." -ForegroundColor Yellow

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    Write-Host "✅ Java: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java not found. Please install Java 17 or later" -ForegroundColor Red
    exit 1
}

# Check Maven
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    Write-Host "✅ Maven: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven not found. Please install Maven" -ForegroundColor Red
    exit 1
}

# Check Node.js
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js not found. Please install Node.js" -ForegroundColor Red
    exit 1
}

# Check npm
try {
    $npmVersion = npm --version
    Write-Host "✅ npm: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ npm not found. Please install npm" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔍 Checking for port conflicts..." -ForegroundColor Yellow

# Check port 8081 (Auth Service)
$port8081 = netstat -ano | findstr ":8081.*LISTENING"
if ($port8081) {
    Write-Host "⚠️  Warning: Port 8081 is already in use" -ForegroundColor Yellow
    Write-Host "   Auth service may fail to start" -ForegroundColor Yellow
}

# Check port 4200 (Frontend)
$port4200 = netstat -ano | findstr ":4200.*LISTENING"
if ($port4200) {
    Write-Host "⚠️  Warning: Port 4200 is already in use" -ForegroundColor Yellow
    Write-Host "   Frontend will use next available port" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🚀 Starting services..." -ForegroundColor Cyan
Write-Host ""

# Start Auth Service
Write-Host "1️⃣  Starting Auth Service (Port 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList `
    "-NoExit", `
    "-Command", `
    "Write-Host '🔐 Auth Service' -ForegroundColor Green; cd '$PSScriptRoot\auth-service'; `$env:SPRING_PROFILES_ACTIVE='dev'; mvn spring-boot:run" `
    -WindowStyle Normal

Write-Host "   ✅ Auth Service terminal opened" -ForegroundColor Green
Start-Sleep -Seconds 2

# Start Frontend
Write-Host "2️⃣  Starting Frontend (Port 4200)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList `
    "-NoExit", `
    "-Command", `
    "Write-Host '🌐 Frontend' -ForegroundColor Green; cd '$PSScriptRoot\edubridge-frontend'; npm run dev" `
    -WindowStyle Normal

Write-Host "   ✅ Frontend terminal opened" -ForegroundColor Green
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Write-Host "   This may take 20-30 seconds..." -ForegroundColor Gray
Write-Host ""

# Wait for Auth Service
$maxAttempts = 30
$attempt = 0
$authRunning = $false

while ($attempt -lt $maxAttempts -and -not $authRunning) {
    Start-Sleep -Seconds 1
    $authCheck = netstat -ano | findstr ":8081.*LISTENING"
    if ($authCheck) {
        $authRunning = $true
        Write-Host "   ✅ Auth Service is running on port 8081" -ForegroundColor Green
    } else {
        $attempt++
        Write-Host "   ⏳ Waiting for Auth Service... ($attempt/$maxAttempts)" -ForegroundColor Gray
    }
}

# Wait for Frontend
$attempt = 0
$frontendRunning = $false

while ($attempt -lt $maxAttempts -and -not $frontendRunning) {
    Start-Sleep -Seconds 1
    $frontendCheck = netstat -ano | findstr ":4200.*LISTENING"
    if ($frontendCheck) {
        $frontendRunning = $true
        Write-Host "   ✅ Frontend is running on port 4200" -ForegroundColor Green
    } else {
        $attempt++
        Write-Host "   ⏳ Waiting for Frontend... ($attempt/$maxAttempts)" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🎉 EduBridge is ready!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($authRunning) {
    Write-Host "✅ Auth Service: http://localhost:8081" -ForegroundColor Green
} else {
    Write-Host "❌ Auth Service: Failed to start (check terminal)" -ForegroundColor Red
}

if ($frontendRunning) {
    Write-Host "✅ Frontend: http://localhost:4200" -ForegroundColor Green
} else {
    Write-Host "❌ Frontend: Failed to start (check terminal)" -ForegroundColor Red
}

Write-Host ""
Write-Host "📱 Next Steps:" -ForegroundColor Yellow
Write-Host "   1. Open http://localhost:4200 in your browser" -ForegroundColor White
Write-Host "   2. Register a new account" -ForegroundColor White
Write-Host "   3. Login with your credentials" -ForegroundColor White
Write-Host "   4. Browse courses and try voice playback! 🔊" -ForegroundColor White
Write-Host ""
Write-Host "📚 Documentation:" -ForegroundColor Yellow
Write-Host "   - COMPLETE_SETUP.md - Full documentation" -ForegroundColor White
Write-Host "   - RUNNING_SERVICES.md - Service management" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Note: Course Service is not started (uses mock data)" -ForegroundColor Yellow
Write-Host "   Frontend has 5 sample courses built-in" -ForegroundColor Gray
Write-Host ""

if ($frontendRunning) {
    Write-Host "🌐 Opening browser..." -ForegroundColor Cyan
    Start-Sleep -Seconds 2
    Start-Process "http://localhost:4200"
}

Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
