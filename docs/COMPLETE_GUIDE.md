# Wildcats Lounge - Complete Application

## 📚 Table of Contents
1. [Project Overview](#project-overview)
2. [Complete Setup Guide](#complete-setup-guide)
3. [Running the Application](#running-the-application)
4. [Testing](#testing)
5. [Documentation](#documentation)

---

## 🎯 Project Overview

**Wildcats Lounge** is a full-stack web application implementing secure user authentication with:
- **Backend:** Spring Boot REST API with MySQL database
- **Frontend:** Next.js with shadcn/ui components
- **Features:** User Registration, Login, Dashboard

---

## 🚀 Complete Setup Guide

### Phase 1: Install Prerequisites

#### 1. Java JDK 17
- Download: https://www.oracle.com/java/technologies/downloads/
- Verify: `java -version`

#### 2. Apache Maven
- Download: https://maven.apache.org/download.cgi
- Add to PATH
- Verify: `mvn -version`

#### 3. MySQL 8.0
- Download: https://dev.mysql.com/downloads/installer/
- **See detailed guide:** [DATABASE_SETUP.md](DATABASE_SETUP.md)
- Verify: `mysql --version`

#### 4. Node.js 18+
- Download: https://nodejs.org/
- Verify: `node --version` and `npm --version`

---

### Phase 2: Setup Backend

```powershell
# Navigate to project root
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge

# Install dependencies
mvn clean install

# Configure database (if password set)
# Edit: src/main/resources/application.properties

# Run backend
mvn spring-boot:run
```

**Backend will run on:** http://localhost:8080

**See complete guide:** [TUTORIAL.md](TUTORIAL.md)

---

### Phase 3: Setup Database

MySQL database is **auto-created** on first run, but for detailed setup:

**See complete guide:** [DATABASE_SETUP.md](DATABASE_SETUP.md)

Quick verification:
```sql
USE wildcatslounge_db;
SHOW TABLES;
DESCRIBE users;
```

---

### Phase 4: Setup Frontend

```powershell
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

**Frontend will run on:** http://localhost:3000

**See complete guide:** [FRONTEND_SETUP.md](FRONTEND_SETUP.md)

---

## 🎮 Running the Application

### Start Both Backend and Frontend

#### Terminal 1 - Backend
```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
mvn spring-boot:run
```

Wait for:
```
========================================
  Wildcats Lounge API is running!
  Access at: http://localhost:8080
========================================
```

#### Terminal 2 - Frontend
```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\frontend
npm run dev
```

Wait for:
```
▲ Next.js 14.2.0
- Local:        http://localhost:3000
```

---

## 🧪 Testing

### Test User Registration

1. **Open browser:** http://localhost:3000
2. **Click "Register Now"**
3. **Fill form:**
   - Name: Maria Santos
   - Email: maria@example.com
   - Password: password123
4. **Submit and verify in database**

### Test User Login

1. **Navigate to:** http://localhost:3000/login
2. **Enter credentials**
3. **Should redirect to dashboard**

### API Testing

Use the included test file:
```
test-api.http
```

Open in VS Code with REST Client extension and click "Send Request"

---

## 📖 Documentation

### Primary Guides

| Document | Description |
|----------|-------------|
| [DATABASE_SETUP.md](DATABASE_SETUP.md) | Complete MySQL installation and configuration |
| [FRONTEND_SETUP.md](FRONTEND_SETUP.md) | Next.js and shadcn/ui setup guide |
| [TUTORIAL.md](TUTORIAL.md) | Comprehensive backend tutorial |
| [QUICK_START.md](QUICK_START.md) | Quick start guide |

### Application URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend Home | http://localhost:3000 | Landing page |
| Registration | http://localhost:3000/register | Create account |
| Login | http://localhost:3000/login | User login |
| Dashboard | http://localhost:3000/dashboard | User dashboard |
| Backend API | http://localhost:8080/api | REST API |
| API Health | http://localhost:8080/api/auth/health | API status |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                    BROWSER                          │
│              http://localhost:3000                  │
└────────────────────┬────────────────────────────────┘
                     │
                     │ HTTP Requests
                     │
┌────────────────────▼────────────────────────────────┐
│              NEXT.JS FRONTEND                       │
│  ┌──────────────────────────────────────────────┐  │
│  │  Pages: Home, Register, Login, Dashboard    │  │
│  │  Components: shadcn/ui (Button, Card, etc)  │  │
│  │  Styling: Tailwind CSS                      │  │
│  └──────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────┘
                     │
                     │ API Calls (Axios)
                     │
┌────────────────────▼────────────────────────────────┐
│           SPRING BOOT BACKEND                       │
│  ┌──────────────────────────────────────────────┐  │
│  │  Controllers: AuthController                 │  │
│  │  Services: UserService                       │  │
│  │  Repositories: UserRepository                │  │
│  │  Entities: User                              │  │
│  └──────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────┘
                     │
                     │ JDBC
                     │
┌────────────────────▼────────────────────────────────┐
│               MYSQL DATABASE                        │
│  ┌──────────────────────────────────────────────┐  │
│  │  Database: wildcatslounge_db                 │  │
│  │  Table: users                                │  │
│  │  Fields: id, name, email, password, etc      │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## ✅ Project Checklist

### Backend
- [x] Spring Boot 3.5.0 configured
- [x] Maven dependencies installed
- [x] MySQL database connected
- [x] User entity created
- [x] Repository layer implemented
- [x] Service layer with business logic
- [x] REST API controllers
- [x] Password encryption (BCrypt)
- [x] Input validation
- [x] CORS enabled for frontend

### Frontend
- [x] Next.js 14 configured
- [x] shadcn/ui components installed
- [x] TypeScript setup
- [x] Tailwind CSS configured
- [x] Registration page
- [x] Login page
- [x] Dashboard page
- [x] API integration layer
- [x] Responsive design
- [x] Error handling

### Database
- [x] MySQL installed
- [x] Database auto-creation configured
- [x] Users table created
- [x] Unique email constraint
- [x] Timestamps configured

---

## 🎓 Submission Requirements

### For IT342 - Phase 1

✅ **GitHub Repository:** IT342-Canadilla-WildcatsLounge

✅ **Features Implemented:**
- User Registration (name, email, password)
- User Login (email, password authentication)
- Password hashing (BCrypt)
- Duplicate email prevention
- Input validation
- Database integration

✅ **Technology Stack:**
- Backend: Spring Boot 3.5.0
- Build Tool: Maven
- Database: MySQL
- Frontend: Next.js 14 + shadcn/ui
- Architecture: REST API

✅ **Documentation:**
- README.md
- TUTORIAL.md
- DATABASE_SETUP.md
- FRONTEND_SETUP.md
- Test files included

---

## 🤝 Support

If you encounter issues:

1. **Check documentation** for your specific problem
2. **Verify all services are running:**
   - MySQL service (port 3306)
   - Backend API (port 8080)
   - Frontend dev server (port 3000)
3. **Check console logs** for error messages
4. **Refer to troubleshooting sections** in each guide

---

## 📝 Developer Notes

- **Name:** Canadilla
- **Course:** IT342
- **Project:** Wildcats Lounge
- **Phase:** 1 - User Registration and Login
- **Date:** March 7, 2026

---

**Application is ready for development and testing! 🚀**
