# Wildcats Lounge - User Management System

> **🔥 PROJECT RECENTLY UPDATED!** Now using **IntelliJ IDEA + Supabase** for simplified setup.  
> **See:** [docs/PROJECT_UPDATED.md](docs/PROJECT_UPDATED.md) for what changed and why.

---

## 📋 Project Overview
Wildcats Lounge is a full-stack web application with user registration and authentication features, built with Spring Boot (backend), Supabase (database), and Next.js (frontend).

**Academic Project:** IT342 - Phase 1 (User Registration and Login)

---

## 🛠️ Technology Stack

### Backend
- **Framework:** Spring Boot 3.5.0
- **Build Tool:** Maven (managed by IntelliJ IDEA)
- **Database:** Supabase (PostgreSQL)
- **Architecture:** REST API
- **Language:** Java 17
- **IDE:** IntelliJ IDEA

### Frontend
- **Framework:** Next.js 14
- **UI Library:** shadcn/ui + Tailwind CSS
- **Language:** TypeScript
- **State Management:** React Hooks

### Database
- **Service:** Supabase
- **Database Type:** PostgreSQL 15
- **Features:** Cloud-hosted, Auto-backups, Dashboard UI

---

## 📦 Project Information
- **Group ID:** `edu.cit.canadilla`
- **Artifact ID:** `wildcatslounge`
- **Base Package:** `edu.cit.canadilla.wildcatslounge`
- **Version:** 1.0.0
- **Repository:** IT342-Canadilla-WildcatsLounge

---

## ✅ Features Implemented (Phase 1)

### User Registration
- ✅ Name, email, and password validation
- ✅ Duplicate email prevention
- ✅ Secure password hashing (BCrypt)

### User Login
- ✅ Email and password authentication
- ✅ Credential validation against database
- ✅ Secure password verification

---

## 🚀 Quick Start Guide

### Prerequisites Checklist
- [ ] **Java 17** installed ([docs/JAVA_SETUP.md](docs/JAVA_SETUP.md))
- [ ] **IntelliJ IDEA** installed (Community or Ultimate)
- [ ] **Supabase account** created ([docs/SUPABASE_SETUP.md](docs/SUPABASE_SETUP.md))
- [ ] **Node.js 18+** (for frontend)

---

### 🎯 Setup Steps (Follow in Order)

#### 1️⃣ Install Java 17
If you have Java 11 (check with `java -version`), upgrade first:
```powershell
java -version  # Should show 17+
```
**If not 17+:** Follow **[docs/JAVA_SETUP.md](docs/JAVA_SETUP.md)**

#### 2️⃣ Set Up Supabase Database
Create your cloud database (no local installation needed):
- Follow: **[docs/SUPABASE_SETUP.md](docs/SUPABASE_SETUP.md)**
- Get connection details (host, password)

#### 3️⃣ Configure Backend
1. Open `backend/src/main/resources/application.properties`
2. Replace placeholders with your Supabase credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres
   spring.datasource.password=YOUR_SUPABASE_PASSWORD
   ```

#### 4️⃣ Run Backend in IntelliJ
- Follow: **[docs/INTELLIJ_SETUP.md](docs/INTELLIJ_SETUP.md)**
- Open backend folder in IntelliJ
- Click Run button in `WildcatsLoungeApplication.java`
- Backend starts at: http://localhost:8080

#### 5️⃣ Run Frontend (Optional)
```powershell
cd web
npm install
npm run dev
```
Frontend starts at: http://localhost:3000

---

## 📚 Documentation

### 🔥 Essential Guides (Start Here!)
1. **[docs/JAVA_SETUP.md](docs/JAVA_SETUP.md)** - Install Java 17 (if you have Java 11)
2. **[docs/SUPABASE_SETUP.md](docs/SUPABASE_SETUP.md)** - Create cloud database (no local install!)
3. **[docs/INTELLIJ_SETUP.md](docs/INTELLIJ_SETUP.md)** - Run backend in IntelliJ IDEA

### 📖 Additional Resources
- **[docs/FRONTEND_SETUP.md](docs/FRONTEND_SETUP.md)** - Frontend setup with Next.js + shadcn/ui
- **[docs/CONFIGURATION_CHECK.md](docs/CONFIGURATION_CHECK.md)** - Verify project configuration
- **[backend/test-api.http](backend/test-api.http)** - API test requests

### 📁 Archived Guides (Old MySQL workflow)
- `docs/MAVEN_SETUP.md` - Not needed (IntelliJ handles Maven)
- `docs/DATABASE_SETUP.md` - Replaced by Supabase
- `docs/TUTORIAL.md`, `docs/COMPLETE_GUIDE.md`, `docs/QUICK_START.md` - Outdated

---

## 📡 API Endpoints

### Health Check
```http
GET http://localhost:8080/api/auth/health
```
**Response:** `{ "status": "OK" }`

### Register User
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:** User object with encrypted password

### Login User
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:** User object if credentials are valid

---

## 📁 Project Structure

```
IT342-Canadilla-WildcatsLounge/
├── backend/                           # Spring Boot REST API
│   ├── src/main/java/edu/cit/canadilla/wildcatslounge/
│   │   ├── WildcatsLoungeApplication.java  ← Run this file in IntelliJ
│   │   ├── controller/               # REST API endpoints
│   │   │   └── AuthController.java   # /api/auth/* endpoints
│   │   ├── service/                  # Business logic
│   │   │   └── UserService.java      # Registration/Login logic
│   │   ├── repository/               # Database access (JPA)
│   │   │   └── UserRepository.java   # User database queries
│   │   ├── entity/                   # Database tables (JPA entities)
│   │   │   └── User.java             # users table
│   │   ├── dto/                      # Data transfer objects
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── UserResponse.java
│   │   │   └── ApiResponse.java
│   │   └── util/                     # Utility classes
│   │       └── PasswordUtil.java     # BCrypt password hashing
│   ├── src/main/resources/
│   │   └── application.properties    ← Supabase configuration
│   ├── pom.xml                       # Maven dependencies (PostgreSQL driver)
│   ├── test-api.http                 # API test requests
│   └── README.md
│
├── web/                               # Next.js Frontend
│   ├── app/                          # Next.js pages (App Router)
│   │   ├── page.tsx                  # Home page
│   │   ├── register/page.tsx         # Registration page
│   │   ├── login/page.tsx            # Login page
│   │   └── dashboard/page.tsx        # User dashboard
│   ├── components/ui/                # shadcn/ui components
│   ├── lib/api.ts                    # API integration (Axios)
│   ├── package.json
│   └── README.md
│
├── docs/                              # Documentation
│   ├── JAVA_SETUP.md                 ← Install Java 17
│   ├── SUPABASE_SETUP.md             ← Create cloud database
│   ├── INTELLIJ_SETUP.md             ← Run backend in IntelliJ
│   ├── FRONTEND_SETUP.md             # Frontend setup
│   └── CONFIGURATION_CHECK.md        # Verify project config
│
├── mobile/                            # Mobile app (future development)
│
└── README.md                          ← You are here
```

---

## 🔐 Security Features

- ✅ **Password Encryption:** BCrypt hashing (10 rounds)
- ✅ **Input Validation:** Email format, password strength
- ✅ **Duplicate Prevention:** Unique email constraint
- ✅ **Secure Connection:** Supabase uses SSL/TLS
- ✅ **Error Handling:** No password leaks in error messages

---

## 🧪 Testing the Application

### Method 1: Use IntelliJ HTTP Client

1. Open `backend/test-api.http` in IntelliJ
2. Click green play buttons next to each request

### Method 2: Use Browser + Frontend

1. Backend running: `http://localhost:8080`
2. Frontend running: `http://localhost:3000`
3. Navigate to: `http://localhost:3000/register`
4. Register a user → Check Supabase Dashboard

### Method 3: Use Postman or cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"pass123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'
```

---

## 🎓 Academic Requirements Compliance

### IT342 Phase 1 Requirements
- ✅ **User Registration:** Implemented with validation
- ✅ **User Login:** Implemented with authentication
- ✅ **Password Validation:** BCrypt encryption
- ✅ **Email Validation:** Format check + unique constraint
- ✅ **Database Integration:** Supabase PostgreSQL
- ✅ **Backend API:** Spring Boot 3.5.0 REST API
- ✅ **Naming Convention:** `edu.cit.canadilla.wildcatslounge`
- ✅ **Maven Project:** Proper `pom.xml` configuration
- ✅ **Repository:** IT342-Canadilla-WildcatsLounge

---

## 🛠️ Development Workflow

### Typical Development Session

1. **Start IntelliJ IDEA**
2. **Open backend folder**
3. **Run `WildcatsLoungeApplication.java`** (green play button)
4. **Backend runs on port 8080**
5. **Open new terminal for frontend:**
   ```powershell
   cd web
   npm run dev
   ```
6. **Frontend runs on port 3000**
7. **Make changes → IntelliJ auto-reloads** (Ctrl+F9 to force rebuild)

### Viewing Database

1. Go to: https://app.supabase.com
2. Select your project
3. Click: **Table Editor**
4. View `users` table with all registered users

---

## 📦 Dependencies

### Backend (Maven - auto-managed by IntelliJ)
```xml
- spring-boot-starter-web 3.5.0
- spring-boot-starter-data-jpa 3.5.0
- spring-boot-starter-validation 3.5.0
- postgresql (PostgreSQL driver)
- spring-security-crypto (BCrypt)
- lombok (Code generation)
- spring-boot-devtools (Hot reload)
```

### Frontend (npm)
```json
- next 14.x
- react 18.x
- typescript 5.x
- tailwindcss 3.x
- shadcn/ui components
- axios (API calls)
```

---

## 🚨 Troubleshooting

### Backend won't start in IntelliJ

**Check:**
1. Java version: `java -version` (should be 17+)
2. Supabase credentials in `application.properties`
3. IntelliJ recognized Maven project (reload if needed)
4. No port 8080 conflicts

**Solution:** See [docs/INTELLIJ_SETUP.md](docs/INTELLIJ_SETUP.md) § Common Issues

### Database connection errors

**Check:**
1. Supabase project is active (not paused - free tier pauses after inactivity)
2. Password is correct in `application.properties`
3. Internet connection is working
4. Firewall not blocking port 5432

**Solution:** See [docs/SUPABASE_SETUP.md](docs/SUPABASE_SETUP.md) § Troubleshooting

### Frontend can't connect to backend

**Check:**
1. Backend is running (`http://localhost:8080/api/auth/health` returns OK)
2. CORS enabled in backend (already configured)
3. Frontend API base URL is correct

---

## 📝 License

This is an academic project for IT342 course requirements.

---

## 👨‍💻 Author

**Canadilla**  
IT342 - Wildcats Lounge Project  
Repository: IT342-Canadilla-WildcatsLounge

---

## 🔗 Useful Links

- **Supabase Dashboard:** https://app.supabase.com
- **IntelliJ IDEA Download:** https://www.jetbrains.com/idea/download/
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Next.js Docs:** https://nextjs.org/docs
- **shadcn/ui:** https://ui.shadcn.com

---

**Last Updated:** March 7, 2026  
**Current Phase:** Phase 1 - User Registration & Login ✅  
**Database:** Supabase PostgreSQL (Cloud-hosted)  
**IDE:** IntelliJ IDEA
- 🗄️ **[docs/DATABASE_SETUP.md](docs/DATABASE_SETUP.md)** - MySQL setup guide
- 🎨 **[docs/FRONTEND_SETUP.md](docs/FRONTEND_SETUP.md)** - Frontend setup with Next.js + shadcn/ui
- 🚀 **[docs/QUICK_START.md](docs/QUICK_START.md)** - Quick setup guide
- 📚 **[docs/COMPLETE_GUIDE.md](docs/COMPLETE_GUIDE.md)** - Complete setup walkthrough

### Project Information
- 📖 **[docs/TUTORIAL.md](docs/TUTORIAL.md)** - Comprehensive backend tutorial
- ✅ **[docs/CONFIGURATION_CHECK.md](docs/CONFIGURATION_CHECK.md)** - Verify project configuration
- 🧪 **[backend/test-api.http](backend/test-api.http)** - API test requests

## Developer
- **Name:** Canadilla
- **Course:** IT342
- **Project:** Wildcats Lounge
- **Phase:** 1 - User Registration and Login

## License
Educational Project - CIT

---
*For complete setup instructions and testing guide, please refer to the documentation in the `docs/` folder.*
