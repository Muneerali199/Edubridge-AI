# Railway CLI Automated Deployment Script
# Deploys EduBridge backend to Railway.app via CLI

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   RAILWAY BACKEND DEPLOYMENT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Railway CLI is installed
Write-Host "Checking Railway CLI..." -ForegroundColor Yellow
$railwayInstalled = Get-Command railway -ErrorAction SilentlyContinue

if (-not $railwayInstalled) {
    Write-Host "❌ Railway CLI not found. Installing..." -ForegroundColor Red
    npm install -g @railway/cli
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "❌ Failed to install Railway CLI" -ForegroundColor Red
        Write-Host "Please run manually: npm install -g @railway/cli" -ForegroundColor Yellow
        exit 1
    }
    Write-Host "✅ Railway CLI installed successfully!" -ForegroundColor Green
} else {
    Write-Host "✅ Railway CLI already installed" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 1: Login to Railway" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Opening browser for authentication..." -ForegroundColor Yellow
Write-Host "Please login with your GitHub account" -ForegroundColor Yellow
Write-Host ""

railway login

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ Railway login failed" -ForegroundColor Red
    Write-Host "Please try again: railway login" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "✅ Successfully logged in to Railway!" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 2: Create Railway Project" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to project root
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

Write-Host "Creating Railway project..." -ForegroundColor Yellow
Write-Host ""
Write-Host "When prompted, choose:" -ForegroundColor Cyan
Write-Host "  1. 'Create a new project'" -ForegroundColor White
Write-Host "  2. Enter name: 'edubridge-ai'" -ForegroundColor White
Write-Host ""

railway init

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ Failed to initialize Railway project" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Railway project created!" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 3: Add PostgreSQL Database" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Adding PostgreSQL plugin..." -ForegroundColor Yellow
railway add --plugin postgresql

if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️ Could not add PostgreSQL via CLI" -ForegroundColor Yellow
    Write-Host "Please add manually:" -ForegroundColor Yellow
    Write-Host "  1. Run: railway open" -ForegroundColor White
    Write-Host "  2. Click 'New' → 'Database' → 'Add PostgreSQL'" -ForegroundColor White
    Write-Host ""
    $continue = Read-Host "Press Enter when PostgreSQL is added..."
} else {
    Write-Host "✅ PostgreSQL database provisioned!" -ForegroundColor Green
    Start-Sleep -Seconds 5
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 4: Deploy Auth Service" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Building and deploying auth-service..." -ForegroundColor Yellow
Write-Host "This may take 5-10 minutes..." -ForegroundColor Gray
Write-Host ""

# Deploy auth service
Set-Location "$projectRoot\auth-service"
railway up

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to deploy auth service" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Auth service deployed!" -ForegroundColor Green
Write-Host ""
Write-Host "Setting environment variables for auth service..." -ForegroundColor Yellow

# Set auth service environment variables
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set JWT_SECRET="your-super-secret-jwt-key-change-this-in-production-min-256-bits"
railway variables set JWT_EXPIRATION=3600000
railway variables set JWT_REFRESH_EXPIRATION=604800000

Write-Host "✅ Environment variables configured!" -ForegroundColor Green

Write-Host ""
Write-Host "Getting auth service URL..." -ForegroundColor Yellow
$authDomain = railway domain

if ($authDomain) {
    Write-Host "✅ Auth Service URL: $authDomain" -ForegroundColor Green
} else {
    Write-Host "⚠️ Could not get domain automatically" -ForegroundColor Yellow
    Write-Host "Run: railway domain" -ForegroundColor White
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 5: Deploy Course Service" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Building and deploying course-service..." -ForegroundColor Yellow
Write-Host "This may take 5-10 minutes..." -ForegroundColor Gray
Write-Host ""

# Deploy course service
Set-Location "$projectRoot\course-service"
railway up

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to deploy course service" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Course service deployed!" -ForegroundColor Green
Write-Host ""
Write-Host "Setting environment variables for course service..." -ForegroundColor Yellow

railway variables set SPRING_PROFILES_ACTIVE=prod

Write-Host "✅ Environment variables configured!" -ForegroundColor Green

Write-Host ""
Write-Host "Getting course service URL..." -ForegroundColor Yellow
$courseDomain = railway domain

if ($courseDomain) {
    Write-Host "✅ Course Service URL: $courseDomain" -ForegroundColor Green
} else {
    Write-Host "⚠️ Could not get domain automatically" -ForegroundColor Yellow
    Write-Host "Run: railway domain" -ForegroundColor White
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STEP 6: Update Frontend URLs" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Your deployed URLs:" -ForegroundColor Yellow
Write-Host ""
if ($authDomain) {
    Write-Host "  Auth API:   https://$authDomain" -ForegroundColor White
}
if ($courseDomain) {
    Write-Host "  Course API: https://$courseDomain" -ForegroundColor White
}
Write-Host ""

Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host ""
Write-Host "  1. Update: edubridge-frontend\src\environments\environment.prod.ts" -ForegroundColor White
Write-Host "     with these URLs:" -ForegroundColor White
Write-Host ""
if ($authDomain) {
    Write-Host "     apiUrl: 'https://$authDomain/auth'" -ForegroundColor Gray
}
if ($courseDomain) {
    Write-Host "     courseApiUrl: 'https://$courseDomain/api'" -ForegroundColor Gray
}
Write-Host ""
Write-Host "  2. Commit and push to GitHub:" -ForegroundColor White
Write-Host "     git add ." -ForegroundColor Gray
Write-Host "     git commit -m 'Update production URLs for Railway'" -ForegroundColor Gray
Write-Host "     git push origin main" -ForegroundColor Gray
Write-Host ""
Write-Host "  3. Netlify will auto-deploy your frontend (2-3 minutes)" -ForegroundColor White
Write-Host ""

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "   DEPLOYMENT COMPLETE! 🚀" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Test your APIs:" -ForegroundColor Cyan
if ($authDomain) {
    Write-Host "  https://$authDomain/auth/health" -ForegroundColor White
}
if ($courseDomain) {
    Write-Host "  https://$courseDomain/api/health" -ForegroundColor White
}
Write-Host ""

Write-Host "Useful Railway commands:" -ForegroundColor Cyan
Write-Host "  railway logs              - View all logs" -ForegroundColor White
Write-Host "  railway open              - Open Railway dashboard" -ForegroundColor White
Write-Host "  railway status            - Check service status" -ForegroundColor White
Write-Host "  railway variables         - View environment variables" -ForegroundColor White
Write-Host ""

Write-Host "Frontend: https://edubridge-ai.netlify.app" -ForegroundColor Yellow
Write-Host ""

# Return to project root
Set-Location $projectRoot
