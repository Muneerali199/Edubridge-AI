# Backend Deployment Guide - Render.com (Free Tier)

## Quick Deploy - 15 Minutes

### 1. Create Render Account
1. Go to [render.com](https://render.com/)
2. Sign up with GitHub
3. Authorize Render to access your GitHub repositories

### 2. Deploy PostgreSQL Database

1. Click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `edubridge-db`
   - **Database**: `edubridge`
   - **User**: `edubridge_user`
   - **Region**: Select closest to you
   - **Plan**: **Free** (limited to 1 database)
3. Click **"Create Database"**
4. Wait 2-3 minutes for deployment
5. Copy the **Internal Database URL** (starts with `postgresql://`)

### 3. Deploy Auth Service

1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repository: `Muneerali199/Edubridge-AI`
3. Configure:
   - **Name**: `edubridge-auth`
   - **Region**: Same as database
   - **Branch**: `main`
   - **Root Directory**: `auth-service`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -jar target/auth-service-0.1.0-SNAPSHOT.jar`
   - **Plan**: **Free**

4. **Environment Variables** (click "Advanced"):
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=<PASTE_INTERNAL_DATABASE_URL_HERE>
   SPRING_DATASOURCE_USERNAME=edubridge_user
   SPRING_DATASOURCE_PASSWORD=<FROM_DATABASE_PAGE>
   JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits
   JWT_EXPIRATION=3600000
   JWT_REFRESH_EXPIRATION=604800000
   ```

5. Click **"Create Web Service"**
6. Wait 5-10 minutes for build and deployment
7. Your auth service URL: `https://edubridge-auth.onrender.com`

### 4. Deploy Course Service

1. Click **"New +"** → **"Web Service"**
2. Connect same GitHub repository
3. Configure:
   - **Name**: `edubridge-courses`
   - **Region**: Same as database
   - **Branch**: `main`
   - **Root Directory**: `course-service`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -jar target/course-service-0.1.0-SNAPSHOT.jar`
   - **Plan**: **Free**

4. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=<SAME_INTERNAL_DATABASE_URL>
   SPRING_DATASOURCE_USERNAME=edubridge_user
   SPRING_DATASOURCE_PASSWORD=<FROM_DATABASE_PAGE>
   ```

5. Click **"Create Web Service"**
6. Wait 5-10 minutes for deployment
7. Your course service URL: `https://edubridge-courses.onrender.com`

### 5. Update Frontend Environment

1. Open: `edubridge-frontend/src/environments/environment.prod.ts`
2. Update:
   ```typescript
   export const environment = {
     production: true,
     apiUrl: 'https://edubridge-auth.onrender.com/auth',
     courseApiUrl: 'https://edubridge-courses.onrender.com/api',
     geminiApiKey: 'AIzaSyCDs-mjox5cYtg1c5GeDhJp-hhoTEi6Cl0'
   };
   ```

3. Commit and push:
   ```bash
   git add .
   git commit -m "Update production API URLs"
   git push origin main
   ```

4. Netlify will auto-deploy (2-3 minutes)

### 6. Test Your Deployment

1. **Frontend**: https://edubridge-ai.netlify.app
2. **Auth API**: https://edubridge-auth.onrender.com/auth/health
3. **Course API**: https://edubridge-courses.onrender.com/api/health

### Important Notes

⚠️ **Free Tier Limitations**:
- Services spin down after 15 minutes of inactivity
- First request after spin-down takes 30-60 seconds
- 750 hours/month free (enough for 1 service 24/7)
- Database: 1GB storage, 97 hours/month uptime

💡 **Tips**:
- Use UptimeRobot (free) to ping services every 5 minutes to keep them awake
- Upgrade to paid plan ($7/month per service) for instant responses
- Database remains free forever

### Troubleshooting

**Build fails?**
- Check Java version in `pom.xml` matches Render's Java 17
- Ensure `mvn clean package` works locally

**Database connection fails?**
- Use **Internal Database URL** (not External)
- Check username and password match database page
- Verify services are in same region as database

**CORS errors?**
- Add Netlify URL to CORS configuration in SecurityConfig
- Deploy updated backend

### Next Steps

✅ All services deployed (auth + course + database + frontend)
✅ Email validation working
✅ Profile management complete
✅ Mobile responsive design
✅ AI Tutor ready

**Your platform is LIVE!** 🎉
