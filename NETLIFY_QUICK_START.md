# 🚀 Quick Netlify Deployment

Deploy EduBridge AI Platform to Netlify in 3 easy steps!

## ⚡ Method 1: GitHub Integration (Recommended - 5 minutes)

### Step 1: Push to GitHub
```powershell
git add .
git commit -m "Ready for Netlify deployment"
git push origin main
```

### Step 2: Deploy on Netlify
1. Go to **[app.netlify.com](https://app.netlify.com)**
2. Click **"Add new site"** → **"Import an existing project"**
3. Select **"Deploy with GitHub"**
4. Choose repository: **Muneerali199/Edubridge-AI**
5. Configure:
   ```
   Base directory: edubridge-frontend
   Build command: npm run build
   Publish directory: dist/edubridge-frontend/browser
   ```
6. Add environment variable:
   - `GEMINI_API_KEY` = `AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0`
7. Click **"Deploy site"**

### Step 3: Done! ✅
Your site will be live at `https://random-name.netlify.app` in 2-3 minutes!

---

## 💻 Method 2: Netlify CLI (Command Line)

### Setup
```powershell
# Install Netlify CLI
npm install -g netlify-cli

# Login to Netlify
netlify login

# Navigate to project
cd "c:\Users\Muneer Ali Subzwari\Desktop\Edubridge"
```

### Deploy
```powershell
# Option A: Use our script
.\scripts\deploy-to-netlify.ps1 -Action init
.\scripts\deploy-to-netlify.ps1 -Action prod -Build

# Option B: Manual commands
cd edubridge-frontend
npm run build
netlify deploy --prod --dir=dist/edubridge-frontend/browser
```

---

## 📤 Method 3: Drag & Drop (Instant)

### Quick Test Deploy
```powershell
# Build the project
cd edubridge-frontend
npm run build

# Output folder: dist/edubridge-frontend/browser/
```

1. Visit **[app.netlify.com/drop](https://app.netlify.com/drop)**
2. Drag the `dist/edubridge-frontend/browser` folder
3. Instantly live! 🎉

---

## ⚙️ Environment Variables

Add these in Netlify dashboard:

| Variable | Value |
|----------|-------|
| `GEMINI_API_KEY` | `AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0` |

**Via CLI:**
```powershell
netlify env:set GEMINI_API_KEY "AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0"
```

---

## 🔧 Backend Options

Your frontend is on Netlify. Deploy backend to:

### Option 1: Render.com (Free)
1. Sign up at [render.com](https://render.com)
2. Click "New +" → "Web Service"
3. Connect GitHub repo
4. Auto-detected: Java + Maven
5. Deploy! (Free tier available)

### Option 2: Railway.app
1. Sign up at [railway.app](https://railway.app)
2. "New Project" → "Deploy from GitHub"
3. Auto-configured
4. Deploy!

### Option 3: Local + ngrok
```powershell
# Expose local backend
ngrok http 8081
# Use ngrok URL in frontend
```

---

## 🌐 Your Live URLs

After deployment:

**Frontend:** `https://your-site.netlify.app`  
**Features:**
- ✅ HTTPS enabled
- ✅ Global CDN
- ✅ Auto-deploy on push
- ✅ Deploy previews for PRs
- ✅ Free 100GB bandwidth/month

---

## 📱 What's Included

✅ AI Tutor with Google Gemini  
✅ Voice Integration (Speech + TTS)  
✅ Beautiful Material Design UI  
✅ Complete Navigation  
✅ Course Management  
✅ User Authentication  
✅ Responsive Design  

---

## 🆘 Troubleshooting

**Build fails?**
```powershell
# Test build locally
cd edubridge-frontend
npm run build

# Check netlify.toml is present
# Check Node version: 20
```

**404 on routes?**
- Already fixed in `netlify.toml`
- All routes redirect to `index.html`

**Environment variables not working?**
1. Add in Netlify dashboard
2. Trigger new deployment
3. Check build logs

---

## 🎯 Full Documentation

- **NETLIFY_DEPLOYMENT.md** - Complete guide
- **netlify.toml** - Configuration file
- **scripts/deploy-to-netlify.ps1** - Deployment script

---

## 💡 Pro Tips

1. **Custom Domain:** Site settings → Domain management
2. **Deploy Previews:** Automatic for all pull requests
3. **Rollbacks:** One-click in deployments tab
4. **Forms:** Built-in form handling (no backend needed)
5. **Functions:** Add serverless functions if needed

---

## 🎉 You're Done!

Your EduBridge AI Platform is now:
- 🌐 Live on Netlify
- 🔒 Secured with HTTPS
- ⚡ Cached on global CDN
- 🔄 Auto-deploying

**Enjoy your deployed platform!** 🚀

---

Need help? Check **NETLIFY_DEPLOYMENT.md** for detailed instructions.
