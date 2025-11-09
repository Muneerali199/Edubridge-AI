# Running EduBridge Services

## Current Status ✅

All services are configured and ready to run. The frontend is currently using mock data due to a known issue with the course-service shutting down after 23 seconds when run in background.

## Services Overview

1. **Auth Service** - Port 8081
   - Status: ✅ Running successfully
   - Handles user authentication and JWT tokens

2. **Course Service** - Port 8082  
   - Status: ⚠️ Configured but auto-shuts down in background
   - Manages courses, instructors, and voice integration

3. **Frontend** - Port 4200
   - Status: ✅ Running successfully
   - Angular application with Material Design UI

## Quick Start

### Option 1: Using Mock Data (Current Setup)

The frontend is currently configured with mock course data, so you only need to run:

```powershell
# Terminal 1: Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2: Frontend
cd edubridge-frontend
npm run dev
```

Open browser: `http://localhost:4200`

### Option 2: With Real Backend (Requires Manual Steps)

If you want to use the real course-service backend:

```powershell
# Terminal 1: Auth Service (Port 8081)
cd C:\Users\Muneer Ali Subzwari\Desktop\Edubridge\auth-service
$env:SPRING_PROFILES_ACTIVE='dev'
mvn spring-boot:run

# Terminal 2: Course Service (Port 8082) - KEEP THIS TERMINAL OPEN
cd C:\Users\Muneer Ali Subzwari\Desktop\Edubridge\course-service
$env:SPRING_PROFILES_ACTIVE='dev'
mvn spring-boot:run

# Terminal 3: Frontend (Port 4200)
cd C:\Users\Muneer Ali Subzwari\Desktop\Edubridge\edubridge-frontend
npm run dev
```

**Important:** Do NOT close Terminal 2. The course-service will shut down if the terminal closes. This is a known issue being investigated.

Then update the frontend to use real API:
- Open `edubridge-frontend/src/app/features/courses/course-list/course-list.component.ts`
- Change `// this.loadCourses();` to `this.loadCourses();`
- Comment out the mock data section

## Features Available

### ✅ Working Features

1. **User Authentication**
   - Register new users
   - Login with email/password
   - JWT token management
   - Protected routes

2. **Course Display**
   - Browse 5 sample courses
   - View course details (title, description, price, ratings)
   - Filter by search query
   - See course levels (Beginner, Intermediate, Advanced)

3. **Voice Integration (Frontend)**
   - Click speaker icon on any course
   - Browser will read course description aloud
   - Uses Web Speech API (no API keys needed)
   - Works on Chrome, Edge, Firefox

4. **Responsive UI**
   - Material Design components
   - Mobile-friendly layout
   - Card-based course display

### 🚧 Known Issues

1. **Course Service Shutdown**
   - Service starts successfully but shuts down after ~23 seconds when run in background
   - Workaround: Keep terminal open or use mock data
   - Investigating: Possible Windows/Java interaction issue

2. **Database Persistence**
   - Currently using H2 in-memory database
   - Data is lost on service restart
   - data.sql script created for sample data

## API Endpoints

### Auth Service (http://localhost:8081/api/auth)
```
POST /register - Create new user
POST /login - Authenticate user
POST /refresh - Refresh JWT token
```

### Course Service (http://localhost:8082/api/courses)
```
GET / - List all courses
GET /{id} - Get course details
POST / - Create new course
PUT /{id} - Update course
DELETE /{id} - Delete course

GET /instructors - List instructors
POST /instructors - Create instructor
```

### Voice Service (http://localhost:8082/api/courses/voice)
```
POST /text-to-speech - Convert text to speech
POST /speech-to-text - Convert speech to text
```

## Testing

### Test Authentication

```powershell
# Register new user
curl -X POST http://localhost:8081/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{"email":"test@example.com","password":"Test123!","name":"Test User"}'

# Login
curl -X POST http://localhost:8081/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"test@example.com","password":"Test123!"}'
```

### Test Course Service (if running)

```powershell
# Get all courses
curl http://localhost:8082/api/courses/

# Get instructors
curl http://localhost:8082/api/courses/instructors
```

## Development

### Build All Services

```powershell
# Root level - builds all modules
mvn clean install

# Or individually
cd common
mvn clean install

cd ../auth-service
mvn clean package

cd ../course-service
mvn clean package
```

### Frontend Development

```powershell
cd edubridge-frontend

# Install dependencies
npm install

# Run dev server
npm run dev

# Build for production
npm run build

# Run tests
npm test
```

## Environment Variables

### Auth Service
```yaml
spring.profiles.active: dev
jwt.secret: edubridge-secret-key-2024-very-secure-and-long
jwt.expiration: 86400000  # 24 hours
```

### Course Service
```yaml
spring.profiles.active: dev
google.tts.enabled: false
google.stt.enabled: false
```

## Next Steps

1. **Fix Course Service Shutdown Issue**
   - Try running as Windows service
   - Deploy to Docker container
   - Investigate Windows process termination

2. **Connect to Real Database**
   - Set up PostgreSQL
   - Update connection strings
   - Enable Flyway migrations

3. **Add More Features**
   - Instructor management UI
   - Course enrollment system
   - Progress tracking
   - Video lecture playback
   - AI tutor integration

4. **Voice Integration Enhancements**
   - Integrate Google Cloud TTS/STT
   - Add voice command navigation
   - Multi-language support

## Troubleshooting

### Port Already in Use
```powershell
# Find process using port
netstat -ano | findstr ":8081"
netstat -ano | findstr ":8082"
netstat -ano | findstr ":4200"

# Kill process by PID
Stop-Process -Id <PID> -Force
```

### Maven Build Fails
```powershell
# Clean everything
mvn clean

# Update dependencies
mvn dependency:resolve

# Skip tests
mvn clean package -DskipTests
```

### Frontend Errors
```powershell
# Clear node_modules and reinstall
Remove-Item -Recurse -Force node_modules
npm install

# Clear Angular cache
npm run clean
```

## Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Angular Docs: https://angular.io/docs
- Material Design: https://material.angular.io
- H2 Database: http://localhost:8082/api/courses/h2-console
- JWT: https://jwt.io

## Contact

For issues or questions, check the GitHub repository or contact the development team.
