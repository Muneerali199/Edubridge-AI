# 🚀 Netlify Deployment Guide - EduBridge AI Platform

Deploy your EduBridge frontend to Netlify in minutes with automatic HTTPS and global CDN!

## 📋 Table of Contents

1. [Quick Deployment (5 minutes)](#quick-deployment-5-minutes)
2. [Prerequisites](#prerequisites)
3. [Option 1: GitHub Integration (Recommended)](#option-1-github-integration-recommended)
4. [Option 2: Netlify CLI](#option-2-netlify-cli)
5. [Option 3: Drag & Drop](#option-3-drag--drop)
6. [Backend Deployment Options](#backend-deployment-options)
7. [Environment Variables](#environment-variables)
8. [Custom Domain Setup](#custom-domain-setup)
9. [Troubleshooting](#troubleshooting)

---

## ⚡ Quick Deployment (5 minutes)

### Step 1: Push to GitHub (if not already done)

```powershell
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"

# Initialize git (if not done)
git init
git add .
git commit -m "Initial commit - EduBridge AI Platform"

# Push to GitHub (already set up)
git push origin main
```

### Step 2: Deploy to Netlify

1. Go to [https://app.netlify.com](https://app.netlify.com)
2. Click **"Add new site"** → **"Import an existing project"**
3. Choose **GitHub** and authorize Netlify
4. Select your **Edubridge-AI** repository
5. Configure build settings:
   - **Base directory:** `edubridge-frontend`
   - **Build command:** `npm run build`
   - **Publish directory:** `dist/edubridge-frontend/browser`
6. Add environment variable:
   - **Key:** `GEMINI_API_KEY`
   - **Value:** `AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0`
7. Click **"Deploy site"**

**That's it!** Your site will be live in 2-3 minutes at `https://random-name.netlify.app`

---

## 📦 Prerequisites

- ✅ GitHub account
- ✅ Netlify account (free - sign up at [netlify.com](https://netlify.com))
- ✅ Code pushed to GitHub repository
- ✅ Node.js 18+ installed

---

## 🎯 Option 1: GitHub Integration (Recommended)

### Advantages
- ✅ Automatic deployments on every push
- ✅ Deploy previews for pull requests
- ✅ Rollback to any previous deployment
- ✅ Free HTTPS with auto-renewal
- ✅ Global CDN

### Setup Steps

#### 1. Create Netlify Configuration File

Already created for you! Check `netlify.toml` in your frontend folder.

#### 2. Connect to Netlify

**Via Netlify Dashboard:**

1. Visit [https://app.netlify.com](https://app.netlify.com)
2. Sign up/Login (you can use GitHub to sign in)
3. Click **"Add new site"** → **"Import an existing project"**
4. Choose **"Deploy with GitHub"**
5. Authorize Netlify to access your GitHub
6. Select repository: **Muneerali199/Edubridge-AI**
7. Configure settings:
   ```
   Base directory: edubridge-frontend
   Build command: npm run build
   Publish directory: dist/edubridge-frontend/browser
   ```
8. Click **"Show advanced"** → **"New variable"**
   - Add: `GEMINI_API_KEY` = `AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0`
9. Click **"Deploy site"**

#### 3. Wait for Build

- Build typically takes 2-5 minutes
- You'll see real-time logs
- Once done, you get a URL like `https://magical-name-123456.netlify.app`

#### 4. Automatic Updates

From now on:
- Every push to `main` → automatic deployment
- Pull requests → deploy previews
- No manual work needed!

---

## 💻 Option 2: Netlify CLI

### Install Netlify CLI

```powershell
# Install globally
npm install -g netlify-cli

# Login to Netlify
netlify login
```

### Deploy from Command Line

```powershell
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\edubridge-frontend"

# Build the project
npm run build

# Deploy to Netlify
netlify deploy --prod

# Follow prompts:
# - Create & configure new site? Yes
# - Choose team
# - Site name: edubridge-ai (or your choice)
# - Publish directory: dist/edubridge-frontend/browser
```

### Set Environment Variables

```powershell
# Set environment variable
netlify env:set GEMINI_API_KEY AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0

# List all environment variables
netlify env:list
```

### Continuous Deployment Setup

```powershell
# Initialize continuous deployment from GitHub
netlify init

# This will:
# 1. Connect to your GitHub repo
# 2. Set up automatic deployments
# 3. Configure build settings
```

---

## 📤 Option 3: Drag & Drop

Perfect for quick testing!

### Steps

```powershell
# 1. Build the project
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\edubridge-frontend"
npm run build

# 2. The build output is in: dist/edubridge-frontend/browser/
```

Now:
1. Go to [https://app.netlify.com/drop](https://app.netlify.com/drop)
2. Drag the `dist/edubridge-frontend/browser` folder to the page
3. Your site is instantly live!

**Note:** This method doesn't support environment variables or automatic updates.

---

## 🔧 Backend Deployment Options

Your frontend is on Netlify, but you need to host the backend. Here are options:

### Option 1: Render.com (Free Tier)

```powershell
# 1. Go to render.com and sign up
# 2. Click "New +" → "Web Service"
# 3. Connect GitHub repo
# 4. Configure:
#    - Name: edubridge-auth
#    - Runtime: Java
#    - Build Command: cd auth-service && mvn clean package -DskipTests
#    - Start Command: java -jar auth-service/target/auth-service-0.1.0-SNAPSHOT.jar
#    - Instance Type: Free
```

### Option 2: Railway.app (Easy)

```powershell
# 1. Go to railway.app and sign up with GitHub
# 2. Click "New Project" → "Deploy from GitHub repo"
# 3. Select Edubridge-AI
# 4. Railway auto-detects Java and Maven
# 5. Add environment variables
# 6. Deploy!
```

### Option 3: Heroku (Classic)

```powershell
# 1. Install Heroku CLI
winget install Heroku.HerokuCLI

# 2. Login
heroku login

# 3. Create apps
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\auth-service"
heroku create edubridge-auth

# 4. Add PostgreSQL
heroku addons:create heroku-postgresql:mini

# 5. Deploy
git push heroku main
```

### Option 4: Keep Local (Development)

For now, you can:
1. Keep backend running locally
2. Use ngrok to expose it:
   ```powershell
   # Install ngrok
   winget install ngrok.ngrok
   
   # Expose port 8081
   ngrok http 8081
   ```
3. Use the ngrok URL in your frontend environment

---

## 🔐 Environment Variables

### Add via Netlify Dashboard

1. Go to your site dashboard
2. Click **"Site settings"** → **"Environment variables"**
3. Click **"Add a variable"**
4. Add these:

```
GEMINI_API_KEY = AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0
API_URL = https://your-backend-url.com
```

### Add via Netlify CLI

```powershell
netlify env:set GEMINI_API_KEY "AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0"
netlify env:set API_URL "https://your-backend-url.com"
```

### Update Environment File

After deploying backend, update `src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-backend-url.com',
  courseApiUrl: 'https://your-backend-url.com',
  geminiApiKey: 'AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0'
};
```

Commit and push - Netlify will auto-deploy!

---

## 🌐 Custom Domain Setup

### Option 1: Use Netlify Domain

Free subdomain included:
- Default: `random-name-123456.netlify.app`
- Rename: Site settings → Domain management → Options → Edit site name
- New URL: `edubridge-ai.netlify.app`

### Option 2: Add Custom Domain

If you own a domain (e.g., `edubridge.com`):

1. Go to **Site settings** → **Domain management**
2. Click **"Add custom domain"**
3. Enter your domain: `edubridge.com`
4. Netlify provides DNS records
5. Add these records to your domain provider:
   ```
   Type: A
   Name: @
   Value: 75.2.60.5
   
   Type: CNAME
   Name: www
   Value: your-site.netlify.app
   ```
6. Wait for DNS propagation (5-60 minutes)
7. Netlify automatically provisions SSL certificate!

---

## 📊 Netlify Features You Get (Free)

✅ **Automatic HTTPS** - Free SSL certificates  
✅ **Global CDN** - Fast delivery worldwide  
✅ **Continuous Deployment** - Auto-deploy on push  
✅ **Deploy Previews** - Preview PRs before merging  
✅ **Rollbacks** - One-click rollback to any version  
✅ **Forms** - Built-in form handling  
✅ **Serverless Functions** - Run backend code on Netlify  
✅ **Analytics** - Basic analytics included  
✅ **100GB Bandwidth** - Free tier includes 100GB/month  
✅ **300 Build Minutes** - More than enough for most projects  

---

## 🔄 Deployment Workflow

Once set up, your workflow is:

```powershell
# 1. Make changes locally
# Edit files in VSCode

# 2. Test locally
cd edubridge-frontend
npm start
# Visit http://localhost:4200

# 3. Commit changes
git add .
git commit -m "Add new feature"

# 4. Push to GitHub
git push origin main

# 5. Netlify automatically:
# ✅ Detects the push
# ✅ Runs build
# ✅ Deploys to production
# ✅ Site live in 2-3 minutes!
```

---

## 🆘 Troubleshooting

### Build Fails on Netlify

**Problem:** Build command fails

**Solution:**
```powershell
# Check your build locally first
cd edubridge-frontend
npm install
npm run build

# If it works locally, check Netlify build logs
# Common issues:
# - Node version mismatch
# - Missing dependencies
# - Environment variables not set
```

Fix Node version in `netlify.toml`:
```toml
[build.environment]
  NODE_VERSION = "20"
```

### 404 on Routes

**Problem:** Direct URLs show 404 (e.g., `/courses`)

**Solution:** Already fixed in `netlify.toml`! It redirects all routes to `index.html`.

If still broken, check:
1. `netlify.toml` exists in `edubridge-frontend/`
2. Publish directory is correct: `dist/edubridge-frontend/browser`

### Environment Variables Not Working

**Problem:** `process.env.GEMINI_API_KEY` is undefined

**Solution:**
1. Add variables in Netlify dashboard
2. Trigger new deploy
3. Check build logs to confirm variables are set

### Build Takes Too Long

**Problem:** Build timeout (15 minutes default)

**Solution:**
```toml
# In netlify.toml
[build]
  command = "npm ci --legacy-peer-deps && npm run build"
```

Use `npm ci` instead of `npm install` for faster, consistent builds.

---

## 💰 Cost Estimate

### Netlify Pricing

| Plan | Price | Bandwidth | Build Minutes | Features |
|------|-------|-----------|---------------|----------|
| **Starter** | **FREE** | 100GB/month | 300 min/month | Perfect for your project! |
| **Pro** | $19/month | 400GB/month | 1000 min/month | Analytics, password protection |
| **Business** | $99/month | 1TB/month | Unlimited | SSO, advanced features |

**Your usage (estimated):**
- Builds: ~5 minutes per deploy × 20 deploys/month = 100 minutes ✅
- Bandwidth: ~500MB per user × 100 users = 50GB ✅
- **Cost: FREE** 🎉

---

## 🚀 Advanced Features

### Deploy Previews

Every pull request gets a unique preview URL!

```powershell
# 1. Create branch
git checkout -b new-feature

# 2. Make changes and push
git push origin new-feature

# 3. Create PR on GitHub
# Netlify automatically creates preview URL
# Share with team before merging!
```

### Netlify Functions (Serverless)

Add backend functionality to Netlify:

```javascript
// netlify/functions/hello.js
exports.handler = async (event, context) => {
  return {
    statusCode: 200,
    body: JSON.stringify({ message: "Hello from Netlify!" })
  };
};
```

Access at: `https://your-site.netlify.app/.netlify/functions/hello`

### Split Testing

Test different versions:

1. Go to **Site settings** → **Split Testing**
2. Create branch variants
3. Netlify splits traffic automatically
4. See which performs better!

---

## 📱 What You Get

After deployment, your platform will have:

🌐 **Live URL**: `https://edubridge-ai.netlify.app`  
🔒 **HTTPS**: Automatic SSL certificate  
🚀 **CDN**: Global content delivery  
⚡ **Fast**: Edge caching for instant loads  
📈 **Scalable**: Handles traffic spikes  
🔄 **Auto-deploy**: Push to deploy  
📊 **Analytics**: Built-in visitor stats  
🔙 **Rollback**: One-click previous version  

---

## 🎯 Complete Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] Netlify account created
- [ ] Site connected to GitHub repo
- [ ] Build settings configured
- [ ] Environment variables added
- [ ] First deployment successful
- [ ] Test all pages and features
- [ ] Backend deployed (Render/Railway/Heroku)
- [ ] Update frontend API URLs
- [ ] Custom domain added (optional)
- [ ] SSL certificate verified
- [ ] Test AI tutor functionality
- [ ] Share with users! 🎉

---

## 📞 Support

### Netlify Resources
- **Documentation**: https://docs.netlify.com
- **Community**: https://answers.netlify.com
- **Status**: https://netlifystatus.com
- **Support**: support@netlify.com

### Quick Commands

```powershell
# Check deployment status
netlify status

# Open site in browser
netlify open

# Open admin dashboard
netlify open:admin

# View recent deploys
netlify deploy:list

# Watch build logs
netlify watch
```

---

## 🎉 You're Live!

Congratulations! Your EduBridge AI Platform is now:

✅ Deployed to Netlify  
✅ Served over HTTPS  
✅ Cached on global CDN  
✅ Auto-deploying on git push  
✅ Accessible worldwide  

**Your Platform Features:**
- 🤖 AI Tutor with Google Gemini
- 🎙️ Voice integration
- 🎨 Beautiful UI
- 📱 Fully responsive
- ⚡ Lightning fast

**Share your site:** `https://your-site.netlify.app`

---

## 🔜 Next Steps

1. **Test Everything**
   - All pages load correctly
   - AI tutor works
   - Voice features work
   - Navigation works

2. **Deploy Backend**
   - Choose: Render, Railway, or Heroku
   - Deploy auth and course services
   - Update frontend API URLs

3. **Monitor Performance**
   - Check Netlify analytics
   - Monitor build times
   - Review bandwidth usage

4. **Share & Grow**
   - Share with users
   - Collect feedback
   - Iterate and improve

**Happy deploying! 🚀**
