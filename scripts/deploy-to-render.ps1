# Deploy to Render.com via Terminal
# This script deploys using Render's Blueprint (Infrastructure as Code)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   RENDER.COM DEPLOYMENT VIA TERMINAL" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Good news! I've created a render.yaml blueprint file." -ForegroundColor Green
Write-Host "This allows you to deploy everything with git push!" -ForegroundColor Green
Write-Host ""

Write-Host "Here's what will be deployed:" -ForegroundColor Yellow
Write-Host "  ✅ PostgreSQL Database (free)" -ForegroundColor White
Write-Host "  ✅ Auth Service (Spring Boot)" -ForegroundColor White
Write-Host "  ✅ Course Service (Spring Boot)" -ForegroundColor White
Write-Host ""

Write-Host "STEP 1: Commit the render.yaml file" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Gray

git add render.yaml
git commit -m "Add Render.com blueprint for automated deployment"
git push origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Pushed to GitHub successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ Git push failed" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "STEP 2: Connect to Render.com" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host ""
Write-Host "Now you need to connect your GitHub repo to Render:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  1. Go to: https://dashboard.render.com/blueprints" -ForegroundColor White
Write-Host "  2. Click 'New Blueprint Instance'" -ForegroundColor White
Write-Host "  3. Connect your GitHub repository: Muneerali199/Edubridge-AI" -ForegroundColor White
Write-Host "  4. Render will auto-detect render.yaml" -ForegroundColor White
Write-Host "  5. Click 'Apply'" -ForegroundColor White
Write-Host ""
Write-Host "That's it! Render will automatically:" -ForegroundColor Cyan
Write-Host "  • Create PostgreSQL database" -ForegroundColor Gray
Write-Host "  • Build and deploy auth-service" -ForegroundColor Gray
Write-Host "  • Build and deploy course-service" -ForegroundColor Gray
Write-Host "  • Configure all environment variables" -ForegroundColor Gray
Write-Host "  • Set up health checks" -ForegroundColor Gray
Write-Host ""
Write-Host "Deployment time: ~10-15 minutes" -ForegroundColor Yellow
Write-Host ""

Write-Host "Alternative: Use Railway (simpler UI)" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host "If Render seems complex, use Railway dashboard instead:" -ForegroundColor Yellow
Write-Host "  railway open" -ForegroundColor White
Write-Host "Then add services via the UI (I already set up PostgreSQL for you!)" -ForegroundColor Gray
Write-Host ""

Write-Host "Once deployed, I'll help you update the frontend URLs!" -ForegroundColor Green
Write-Host ""
