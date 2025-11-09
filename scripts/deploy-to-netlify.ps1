# Netlify Deployment Script for EduBridge AI Platform
# Quick deploy your frontend to Netlify

param(
    [Parameter()]
    [ValidateSet('init', 'deploy', 'prod', 'status', 'open')]
    [string]$Action = 'deploy',
    
    [Parameter()]
    [switch]$Build
)

$ErrorActionPreference = "Stop"
$FrontendPath = "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\edubridge-frontend"

# Color output functions
function Write-Success { Write-Host "✅ $args" -ForegroundColor Green }
function Write-Info { Write-Host "ℹ️  $args" -ForegroundColor Cyan }
function Write-Warning { Write-Host "⚠️  $args" -ForegroundColor Yellow }
function Write-Error-Custom { Write-Host "❌ $args" -ForegroundColor Red }

# Banner
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                           ║" -ForegroundColor Cyan
Write-Host "║        EduBridge AI - Netlify Deployment                  ║" -ForegroundColor Cyan
Write-Host "║                                                           ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check if Netlify CLI is installed
try {
    $netlifyVersion = netlify --version 2>&1
    Write-Success "Netlify CLI installed: $netlifyVersion"
} catch {
    Write-Warning "Netlify CLI not found. Installing..."
    npm install -g netlify-cli
    Write-Success "Netlify CLI installed successfully!"
}

# Navigate to frontend directory
Set-Location $FrontendPath

switch ($Action) {
    'init' {
        Write-Info "Initializing Netlify deployment..."
        Write-Host ""
        Write-Info "This will:"
        Write-Host "  1. Connect to your Netlify account" -ForegroundColor Gray
        Write-Host "  2. Link to your GitHub repository" -ForegroundColor Gray
        Write-Host "  3. Set up continuous deployment" -ForegroundColor Gray
        Write-Host ""
        
        netlify init
        
        Write-Success "Netlify initialized!"
        Write-Info "Next steps:"
        Write-Host "  1. Configure environment variables in Netlify dashboard"
        Write-Host "  2. Push to GitHub to trigger automatic deployment"
        Write-Host "  3. Or run: .\scripts\deploy-to-netlify.ps1 -Action prod"
    }
    
    'deploy' {
        Write-Info "Deploying to Netlify draft (preview)..."
        
        if ($Build) {
            Write-Info "Building project..."
            npm run build
            if ($LASTEXITCODE -ne 0) {
                Write-Error-Custom "Build failed!"
                exit 1
            }
            Write-Success "Build completed!"
        }
        
        Write-Info "Deploying to Netlify..."
        netlify deploy
        
        Write-Success "Draft deployed!"
        Write-Info "Review the preview URL above"
        Write-Info "If everything looks good, run: .\scripts\deploy-to-netlify.ps1 -Action prod"
    }
    
    'prod' {
        Write-Info "Deploying to Netlify PRODUCTION..."
        Write-Warning "This will update your live site!"
        Write-Host ""
        
        $confirm = Read-Host "Continue? (y/n)"
        if ($confirm -ne 'y') {
            Write-Info "Deployment cancelled"
            exit 0
        }
        
        if ($Build) {
            Write-Info "Building project..."
            npm run build
            if ($LASTEXITCODE -ne 0) {
                Write-Error-Custom "Build failed!"
                exit 1
            }
            Write-Success "Build completed!"
        }
        
        Write-Info "Deploying to production..."
        netlify deploy --prod
        
        Write-Success "Production deployment complete!"
        Write-Info "Your site is live!"
    }
    
    'status' {
        Write-Info "Checking Netlify status..."
        netlify status
        Write-Host ""
        netlify sites:list
    }
    
    'open' {
        Write-Info "Opening Netlify dashboard..."
        netlify open:admin
        Write-Host ""
        Write-Info "Opening live site..."
        netlify open:site
    }
}

Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                                                           ║" -ForegroundColor Green
Write-Host "║                 Netlify Operation Complete!               ║" -ForegroundColor Green
Write-Host "║                                                           ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Info "Useful commands:"
Write-Host "  netlify status          - Check deployment status" -ForegroundColor Gray
Write-Host "  netlify open            - Open live site" -ForegroundColor Gray
Write-Host "  netlify open:admin      - Open Netlify dashboard" -ForegroundColor Gray
Write-Host "  netlify logs            - View function logs" -ForegroundColor Gray
Write-Host "  netlify env:list        - List environment variables" -ForegroundColor Gray
Write-Host ""
