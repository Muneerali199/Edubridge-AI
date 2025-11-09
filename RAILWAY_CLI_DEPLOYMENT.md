# Railway CLI Deployment - Automated with Terminal Commands

## Why Railway?
- ✅ **Full CLI support** - Deploy everything via terminal
- ✅ **Completely FREE** for hobby projects (no credit card required)
- ✅ **Faster than Render** - No spin-down on free tier
- ✅ **Simple commands** - One command per service
- ✅ **PostgreSQL included** - Free database with instant setup

## Prerequisites
- Node.js installed (you already have this)
- Git repository pushed to GitHub (✅ done)
- Terminal/PowerShell access

## Quick Deploy - 10 Minutes via CLI

### Step 1: Install Railway CLI

```powershell
# Install Railway CLI via npm
npm install -g @railway/cli

# Login to Railway (opens browser for auth)
railway login
```

### Step 2: Initialize Railway Project

```powershell
# Navigate to project root
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"

# Link to GitHub repo and create Railway project
railway init
# Follow prompts:
# - "Create new project" → Enter project name: "edubridge-ai"
# - Link to GitHub repo: Muneerali199/Edubridge-AI
```

### Step 3: Deploy PostgreSQL Database

```powershell
# Add PostgreSQL plugin
railway add --database postgres

# Railway automatically provisions PostgreSQL and sets environment variables
# The DATABASE_URL will be available to all services
```

### Step 4: Deploy Auth Service

```powershell
# Deploy auth service
railway up --service auth-service --directory auth-service

# Set environment variables
railway variables set SPRING_PROFILES_ACTIVE=prod --service auth-service
railway variables set JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits --service auth-service
railway variables set JWT_EXPIRATION=3600000 --service auth-service
railway variables set JWT_REFRESH_EXPIRATION=604800000 --service auth-service

# Railway will automatically build using your Dockerfile or detect Maven
# Get the public URL
railway domain --service auth-service
```

### Step 5: Deploy Course Service

```powershell
# Deploy course service
railway up --service course-service --directory course-service

# Set environment variables
railway variables set SPRING_PROFILES_ACTIVE=prod --service course-service

# Get the public URL
railway domain --service course-service
```

### Step 6: Update Frontend URLs

```powershell
# Railway will give you URLs like:
# Auth: https://auth-service-production-xxxx.up.railway.app
# Course: https://course-service-production-xxxx.up.railway.app

# Update environment file (replace with your actual URLs)
$authUrl = railway domain --service auth-service
$courseUrl = railway domain --service course-service

Write-Host "Auth Service: $authUrl"
Write-Host "Course Service: $courseUrl"
```

Then manually update `edubridge-frontend/src/environments/environment.prod.ts` with these URLs.

### Step 7: Push Changes

```powershell
git add .
git commit -m "Update production URLs for Railway deployment"
git push origin main
# Netlify auto-deploys frontend
```

## Alternative: Use the Automated Script

I've created `deploy-railway.ps1` that does everything automatically!

```powershell
# Make sure Railway CLI is installed first
npm install -g @railway/cli
railway login

# Run the deployment script
.\scripts\deploy-railway.ps1
```

## Monitoring & Management

```powershell
# View logs for auth service
railway logs --service auth-service

# View logs for course service
railway logs --service course-service

# Check service status
railway status

# Open Railway dashboard
railway open
```

## Environment Variables Reference

Railway automatically provides `DATABASE_URL` from PostgreSQL plugin.

**Auth Service needs:**
- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET=<your-secret>`
- `JWT_EXPIRATION=3600000`
- `JWT_REFRESH_EXPIRATION=604800000`

**Course Service needs:**
- `SPRING_PROFILES_ACTIVE=prod`

## Advantages over Render.com

| Feature | Railway (Free) | Render (Free) |
|---------|---------------|---------------|
| CLI Support | ✅ Full | ⚠️ Limited |
| Spin-down | ❌ Never | ✅ After 15min |
| Cold Start | ⚡ Instant | 🐌 30-60 sec |
| Build Speed | ⚡ Fast | 🐌 Slower |
| Database | ✅ Free PostgreSQL | ✅ Free PostgreSQL |
| Monthly Hours | ✅ 500 hours | ✅ 750 hours |
| Deployment | 🚀 One command | 🖱️ Web UI required |

## Free Tier Limits

- **$5 credit/month** (enough for small apps)
- **500 execution hours/month**
- **100GB outbound bandwidth/month**
- **PostgreSQL**: 1GB storage

## Troubleshooting

**Railway CLI not found?**
```powershell
npm install -g @railway/cli
```

**Authentication failed?**
```powershell
railway logout
railway login
```

**Build failed?**
```powershell
# View build logs
railway logs --service auth-service

# Redeploy
railway up --service auth-service --directory auth-service
```

**Database connection failed?**
Railway automatically injects `DATABASE_URL`. Ensure your `application-prod.yml` uses it:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
```

## Next Steps After Deployment

1. ✅ Test auth API: `https://your-auth-url/auth/health`
2. ✅ Test course API: `https://your-course-url/api/health`
3. ✅ Test frontend: `https://edubridge-ai.netlify.app`
4. ✅ Register a user and test profile management
5. ✅ Test AI Tutor functionality
6. ✅ Test on mobile devices

## Cost Estimate

**Free Forever:**
- Frontend on Netlify: $0/month
- Backend on Railway: $0/month (within free tier)
- Total: **$0/month** 🎉

**If You Exceed Free Tier:**
- Railway charges **$5 per service/month**
- You'll get email alerts before charges apply
- Can set spending limits in Railway dashboard

---

**Your complete platform deployed via CLI in ~10 minutes!** 🚀
