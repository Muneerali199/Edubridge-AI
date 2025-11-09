# SUPER QUICK RAILWAY DEPLOYMENT
# Just 3 commands to deploy everything!

Write-Host ""
Write-Host "🚀 EduBridge Railway Deployment - Quick Start" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Install Railway CLI
Write-Host "Step 1: Installing Railway CLI..." -ForegroundColor Yellow
npm install -g @railway/cli

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to install Railway CLI" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Railway CLI installed!" -ForegroundColor Green
Write-Host ""

# Step 2: Login
Write-Host "Step 2: Login to Railway (browser will open)..." -ForegroundColor Yellow
railway login

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Login failed" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Logged in successfully!" -ForegroundColor Green
Write-Host ""

# Step 3: Deploy
Write-Host "Step 3: Choose deployment option:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  [1] Automated deployment (recommended)" -ForegroundColor White
Write-Host "  [2] Manual step-by-step" -ForegroundColor White
Write-Host "  [3] See deployment guide" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter choice (1-3)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Running automated deployment script..." -ForegroundColor Green
        & "$PSScriptRoot\deploy-railway.ps1"
    }
    "2" {
        Write-Host ""
        Write-Host "Manual Deployment Steps:" -ForegroundColor Cyan
        Write-Host "========================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "1. Create project and add database:" -ForegroundColor Yellow
        Write-Host "   railway init" -ForegroundColor White
        Write-Host "   railway add --plugin postgresql" -ForegroundColor White
        Write-Host ""
        Write-Host "2. Deploy auth service:" -ForegroundColor Yellow
        Write-Host "   cd auth-service" -ForegroundColor White
        Write-Host "   railway up" -ForegroundColor White
        Write-Host "   railway variables set SPRING_PROFILES_ACTIVE=prod" -ForegroundColor White
        Write-Host "   railway variables set JWT_SECRET=your-secret-key" -ForegroundColor White
        Write-Host "   railway domain" -ForegroundColor White
        Write-Host ""
        Write-Host "3. Deploy course service:" -ForegroundColor Yellow
        Write-Host "   cd ../course-service" -ForegroundColor White
        Write-Host "   railway up" -ForegroundColor White
        Write-Host "   railway variables set SPRING_PROFILES_ACTIVE=prod" -ForegroundColor White
        Write-Host "   railway domain" -ForegroundColor White
        Write-Host ""
        Write-Host "4. Update frontend URLs and push to GitHub" -ForegroundColor Yellow
        Write-Host ""
    }
    "3" {
        Write-Host ""
        Write-Host "Opening deployment guide..." -ForegroundColor Green
        $guidePath = Join-Path (Split-Path -Parent $PSScriptRoot) "RAILWAY_CLI_DEPLOYMENT.md"
        if (Test-Path $guidePath) {
            code $guidePath
        } else {
            Write-Host "Guide location: RAILWAY_CLI_DEPLOYMENT.md" -ForegroundColor White
        }
    }
    default {
        Write-Host ""
        Write-Host "Invalid choice. Run the script again." -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "📚 Documentation:" -ForegroundColor Cyan
Write-Host "  - Full guide: RAILWAY_CLI_DEPLOYMENT.md" -ForegroundColor White
Write-Host "  - Railway docs: https://docs.railway.app" -ForegroundColor White
Write-Host ""
