# 🚀 Deploy Backend with Terminal Commands

## Option 1: Super Quick (Recommended) - 3 Commands

```powershell
# Install Railway CLI
npm install -g @railway/cli

# Login (opens browser)
railway login

# Run automated deployment
.\scripts\quick-deploy.ps1
```

**That's it!** The script handles everything automatically.

---

## Option 2: One-Line Deployment

```powershell
.\scripts\deploy-railway.ps1
```

This automated script will:
- ✅ Check/install Railway CLI
- ✅ Login to Railway
- ✅ Create project
- ✅ Add PostgreSQL database
- ✅ Deploy auth-service
- ✅ Deploy course-service  
- ✅ Configure environment variables
- ✅ Give you the URLs

**Time**: ~10 minutes

---

## Option 3: Manual Commands (Full Control)

### Step 1: Setup
```powershell
npm install -g @railway/cli
railway login
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"
railway init
```

### Step 2: Add Database
```powershell
railway add --plugin postgresql
```

### Step 3: Deploy Auth Service
```powershell
cd auth-service
railway up
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits
railway variables set JWT_EXPIRATION=3600000
railway variables set JWT_REFRESH_EXPIRATION=604800000
railway domain
```

### Step 4: Deploy Course Service
```powershell
cd ../course-service
railway up
railway variables set SPRING_PROFILES_ACTIVE=prod
railway domain
```

### Step 5: Update Frontend
Copy the URLs from `railway domain` and update:
`edubridge-frontend\src\environments\environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-auth-url/auth',
  courseApiUrl: 'https://your-course-url/api',
  geminiApiKey: 'AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0'
};
```

Then push:
```powershell
git add .
git commit -m "Update production URLs"
git push origin main
```

---

## Useful Railway Commands

```powershell
# View logs
railway logs

# Open dashboard
railway open

# Check status
railway status

# View environment variables
railway variables

# Redeploy
railway up --detach

# Link to existing project
railway link
```

---

## Why Railway CLI?

✅ **100% Terminal-based** - No web UI needed  
✅ **Fast deployment** - One command per service  
✅ **Free tier** - $5/month credit (enough for hobby projects)  
✅ **No spin-down** - Services stay active  
✅ **Instant cold starts** - No 30-60 sec delays  
✅ **PostgreSQL included** - Free database  

---

## Comparison

| Method | Time | Commands | Difficulty |
|--------|------|----------|------------|
| **Quick Script** | 10 min | 3 | ⭐ Easy |
| **Automated Script** | 10 min | 1 | ⭐ Easy |
| **Manual CLI** | 15 min | ~12 | ⭐⭐ Medium |
| **Render.com Web UI** | 20 min | 0 | ⭐⭐⭐ Manual |

---

## Troubleshooting

**Railway CLI not found?**
```powershell
npm install -g @railway/cli
refreshenv  # or restart PowerShell
```

**Permission denied?**
```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

**Build failed?**
```powershell
railway logs  # Check error logs
railway up --detach  # Retry deployment
```

---

## What Gets Deployed?

✅ **Auth Service** (Port 8081)
- User authentication
- JWT tokens
- Profile management
- Email validation

✅ **Course Service** (Port 8082)
- Course management
- Enrollment system

✅ **PostgreSQL Database**
- User data
- Course data
- Shared between services

✅ **Frontend** (Netlify - auto-deploys)
- Angular app
- AI Tutor
- Mobile responsive

---

## After Deployment

Test your APIs:
```powershell
# Auth service health check
curl https://your-auth-url/auth/health

# Course service health check
curl https://your-course-url/api/health

# Frontend
# Visit: https://edubridge-ai.netlify.app
```

---

## Total Cost

**$0/month** within free tier limits:
- Railway: $5 credit/month (covers 2 services)
- Netlify: Unlimited for open source
- PostgreSQL: 1GB free on Railway

---

**Ready to deploy? Run:** `.\scripts\quick-deploy.ps1` 🚀
