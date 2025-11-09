# 🚀 EduBridge Quick Start Guide

## ✅ Both Services Are Running!

### Backend (Spring Boot)
- **URL**: http://localhost:8081/api/auth
- **Status**: ✅ RUNNING
- **Database**: H2 In-Memory
- **H2 Console**: http://localhost:8081/api/auth/h2-console (JDBC URL: `jdbc:h2:mem:edubridge`, User: `sa`, Password: blank)

### Frontend (Angular 20)
- **URL**: http://localhost:4200
- **Status**: ✅ RUNNING
- **Routes**:
  - `/auth/login` - Login page
  - `/auth/register` - Registration page  
  - `/dashboard` - Protected dashboard

## 🧪 Test the Integration Now!

### Option 1: Test via Browser (Recommended)
1. **Open**: http://localhost:4200
2. **Navigate to**: Registration page (`/auth/register`)
3. **Fill the form**:
   - First Name: John
   - Last Name: Doe
   - Email: john.doe@edubridge.com
   - Password: Test@1234
   - Confirm Password: Test@1234
   - Role: STUDENT
4. **Click "Register"**
5. You should be redirected to `/dashboard` with a welcome message!

### Option 2: Test via API (PowerShell)

#### Register a User
```powershell
$body = @{
    firstName = "Jane"
    lastName = "Smith"
    email = "jane.smith@edubridge.com"
    password = "Test@1234"
    role = "STUDENT"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8081/api/auth/register" `
    -ContentType "application/json" `
    -Body $body
```

#### Login
```powershell
$body = @{
    email = "jane.smith@edubridge.com"
    password = "Test@1234"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8081/api/auth/login" `
    -ContentType "application/json" `
    -Body $body
```

## 🔍 What to Check

### In Browser (F12 Developer Tools):

1. **Network Tab**
   - Watch for API calls to `/api/auth/register` and `/api/auth/login`
   - Check request/response payloads
   - Verify JWT tokens in response

2. **Application Tab → Local Storage**
   - After login, you should see:
     - `access_token`: Your JWT access token
     - `refresh_token`: Your JWT refresh token
     - `user_data`: Your user information

3. **Console Tab**
   - Should have no errors
   - May see Angular debug messages

### In Backend Logs:

Look for these log messages in the terminal running Spring Boot:
```
2025-11-09 15:31:25 - Tomcat started on port 8081 (http) with context path '/api/auth'
2025-11-09 15:31:25 - Started AuthServiceApplication in 3.943 seconds
```

When you make API calls, you'll see:
```
Hibernate: insert into public.users (...)
```

## 🎯 Features to Test

### 1. Registration Flow
- ✅ Form validation (required fields, email format, password strength)
- ✅ Password confirmation match
- ✅ Role selection (dropdown)
- ✅ Success message (snackbar)
- ✅ Auto-redirect to dashboard
- ✅ Token storage in localStorage

### 2. Login Flow
- ✅ Email/password validation
- ✅ Show/hide password toggle
- ✅ Error handling (invalid credentials)
- ✅ Success redirect to dashboard
- ✅ "Remember me" placeholder (future feature)

### 3. Dashboard
- ✅ Display user name
- ✅ Display user email
- ✅ Display user role
- ✅ Logout button
- ✅ Logout clears tokens and redirects to login

### 4. Auth Guard (Route Protection)
- Try accessing `/dashboard` when logged out
- Should redirect to `/auth/login?returnUrl=/dashboard`
- After login, should redirect back to dashboard

### 5. JWT Interceptor
- Check Network tab
- All authenticated requests should have `Authorization: Bearer <token>` header

## 🛠️ Troubleshooting

### Backend Not Responding?
```powershell
# Check if port 8081 is in use
netstat -ano | findstr :8081

# Restart backend
cd 'c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\auth-service'
$env:SPRING_PROFILES_ACTIVE='dev'
mvn spring-boot:run
```

### Frontend Not Loading?
```powershell
# Check if port 4200 is in use
netstat -ano | findstr :4200

# Restart frontend
cd 'c:\Users\Muneer Ali Subzwari\Desktop\Edubridge\edubridge-frontend'
npm run start
```

### CORS Errors?
- Backend `SecurityConfig.java` allows `http://localhost:4200`
- Frontend uses proxy configuration in `proxy.conf.json`
- Both should work together seamlessly

## 📊 Current Endpoints

### Public Endpoints (No Auth Required)
- `POST /api/auth/register` - Create new user account
- `POST /api/auth/login` - Authenticate user
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/h2-console` - H2 Database console

### Protected Endpoints (Auth Required)
- `POST /api/auth/logout` - Logout user
- Future: `/api/users/**`, `/api/content/**`, etc.

## 🎉 Success!

You now have a fully integrated **Spring Boot + Angular** authentication system with:

- ✅ JWT-based authentication
- ✅ Role-based access control ready
- ✅ Password hashing with BCrypt
- ✅ HTTP-only security best practices
- ✅ Modern Angular 20 with Material Design
- ✅ Reactive forms with validation
- ✅ Route guards and interceptors
- ✅ H2 in-memory database for development

## 🚀 Next Steps

1. Test the complete authentication flow in the browser
2. Check H2 console to see user data: http://localhost:8081/api/auth/h2-console
3. Explore the codebase to understand the architecture
4. Start building additional features:
   - Email verification
   - Password reset
   - User profile management
   - Additional microservices

---

**Happy Coding! 🎓**
