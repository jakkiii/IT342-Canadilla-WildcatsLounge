# 🎓 WILDCATS LOUNGE - COMPLETE SETUP TUTORIAL

## Project Information
- **Group ID:** edu.cit.canadilla
- **Artifact ID:** wildcatslounge
- **Package:** edu.cit.canadilla.wildcatslounge
- **Spring Boot Version:** 3.5.0
- **Java Version:** 17

---

## 📋 PREREQUISITES

Before starting, ensure you have installed:

### 1. Java Development Kit (JDK) 17 or higher
**Download:** https://www.oracle.com/java/technologies/downloads/

**Verify installation:**
```bash
java -version
```
Should show: `java version "17"` or higher

### 2. Apache Maven 3.6+
**Download:** https://maven.apache.org/download.cgi

**Installation Steps:**
1. Download Maven binary zip archive
2. Extract to `C:\Program Files\Apache\maven`
3. Add Maven to System PATH:
   - Open System Properties → Environment Variables
   - Add to PATH: `C:\Program Files\Apache\maven\bin`

**Verify installation:**
```bash
mvn -version
```

### 3. MySQL Database 8.0+
**Download:** https://dev.mysql.com/downloads/installer/

**Installation Steps:**
1. Download MySQL Installer
2. Select "Developer Default" installation
3. Set root password (leave empty for this tutorial, or update application.properties)
4. Complete installation

**Verify installation:**
```bash
mysql --version
```

### 4. IDE (Choose one)
- **VS Code** (Recommended) + Extension Pack for Java
- **IntelliJ IDEA Community Edition**
- **Eclipse IDE for Java Developers**

---

## 🚀 STEP-BY-STEP SETUP GUIDE

### STEP 1: Verify Project Structure

Your project should have this structure:
```
IT342-Canadilla-WildcatsLounge/
├── pom.xml
├── .gitignore
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/
│       │       └── cit/
│       │           └── canadilla/
│       │               └── wildcatslounge/
│       │                   ├── WildcatsLoungeApplication.java
│       │                   ├── controller/
│       │                   │   └── AuthController.java
│       │                   ├── service/
│       │                   │   └── UserService.java
│       │                   ├── repository/
│       │                   │   └── UserRepository.java
│       │                   ├── entity/
│       │                   │   └── User.java
│       │                   ├── dto/
│       │                   │   ├── RegisterRequest.java
│       │                   │   ├── LoginRequest.java
│       │                   │   ├── UserResponse.java
│       │                   │   └── ApiResponse.java
│       │                   └── util/
│       │                       └── PasswordUtil.java
│       └── resources/
│           └── application.properties
└── TUTORIAL.md (this file)
```

### STEP 2: Configure Database

1. **Open MySQL Workbench or Command Line**

2. **The database will be created automatically when you run the application**
   - Database name: `wildcatslounge_db`
   - This is configured in `application.properties`

3. **If you set a MySQL root password, update `application.properties`:**
```properties
spring.datasource.password=YOUR_PASSWORD_HERE
```

### STEP 3: Install Dependencies with Maven

Open terminal in the project root directory and run:

```bash
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
mvn clean install
```

**What this does:**
- `clean` - Removes previous build files
- `install` - Downloads all dependencies from pom.xml and builds the project

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45.678 s
```

### STEP 4: Run the Application

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using Java**
```bash
mvn package
java -jar target/wildcatslounge-1.0.0.jar
```

**Expected output:**
```
========================================
  Wildcats Lounge API is running!
  Access at: http://localhost:8080
========================================
```

### STEP 5: Test the API

The application exposes the following endpoints:

#### Endpoint 1: Health Check
```
GET http://localhost:8080/api/auth/health
```

#### Endpoint 2: User Registration
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Juan Dela Cruz",
  "email": "juan@example.com",
  "password": "password123"
}
```

#### Endpoint 3: User Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

---

## 🧪 TESTING THE API

### Method 1: Using VS Code REST Client Extension

1. Install "REST Client" extension in VS Code
2. Create a file: `test-api.http`
3. Add test requests:

```http
### Health Check
GET http://localhost:8080/api/auth/health

### Register User
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Maria Santos",
  "email": "maria@example.com",
  "password": "secure123"
}

### Login User
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "maria@example.com",
  "password": "secure123"
}
```

4. Click "Send Request" above each request

### Method 2: Using PowerShell (Invoke-RestMethod)

**Health Check:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method GET
```

**Register:**
```powershell
$body = @{
    name = "Pedro Garcia"
    email = "pedro@example.com"
    password = "mypassword"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

**Login:**
```powershell
$body = @{
    email = "pedro@example.com"
    password = "mypassword"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

### Method 3: Using Postman

1. Download Postman: https://www.postman.com/downloads/
2. Create a new collection "Wildcats Lounge API"
3. Add requests as shown in the endpoints above

---

## 📊 VERIFY DATABASE

After testing, verify data in MySQL:

```sql
USE wildcatslounge_db;

-- View all users
SELECT * FROM users;

-- Check specific user
SELECT id, name, email, created_at FROM users WHERE email = 'maria@example.com';
```

---

## 🎯 PROJECT ARCHITECTURE

### Layered Architecture

```
┌─────────────────────────────────────┐
│      Controller Layer               │  ← REST API Endpoints
│   (AuthController.java)             │     Handle HTTP Requests
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Service Layer                 │  ← Business Logic
│    (UserService.java)               │     Validation, Processing
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Repository Layer                │  ← Database Operations
│   (UserRepository.java)             │     CRUD Operations
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Database (MySQL)             │  ← Data Storage
│     wildcatslounge_db               │
└─────────────────────────────────────┘
```

### Package Structure Explanation

- **entity/** - Database table definitions (JPA entities)
- **repository/** - Database access layer (Spring Data JPA)
- **service/** - Business logic and validation
- **controller/** - REST API endpoints
- **dto/** - Data Transfer Objects (request/response formats)
- **util/** - Utility classes (password hashing)

---

## 🔒 SECURITY FEATURES IMPLEMENTED

1. **Password Hashing**
   - Uses BCrypt algorithm
   - Passwords are never stored in plain text
   - Implemented in `PasswordUtil.java`

2. **Input Validation**
   - Email format validation
   - Password minimum length (6 characters)
   - Name length validation (2-100 characters)
   - Required field checks

3. **Duplicate Prevention**
   - Email uniqueness enforced at database level
   - Application-level check before registration

4. **Error Handling**
   - Validated user inputs
   - Clear error messages
   - Proper HTTP status codes

---

## 📝 API RESPONSE FORMATS

### Success Response
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "Maria Santos",
    "email": "maria@example.com",
    "createdAt": "2026-03-07 14:30:00"
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Email already registered",
  "data": null
}
```

---

## 🐛 COMMON ISSUES AND SOLUTIONS

### Issue 1: Port 8080 already in use
**Solution:**
```properties
# Change port in application.properties
server.port=8081
```

### Issue 2: Cannot connect to MySQL
**Solution:**
1. Verify MySQL is running
2. Check username/password in application.properties
3. Ensure database exists or set `createDatabaseIfNotExist=true`

### Issue 3: Maven build fails
**Solution:**
```bash
# Clear Maven cache and rebuild
mvn clean
mvn install -U
```

### Issue 4: Java version mismatch
**Solution:**
```bash
# Check Java version
java -version

# Update JAVA_HOME environment variable to JDK 17+
```

---

## 📈 NEXT STEPS

After completing Phase 1, you can extend the application:

1. **Add Session Management** - Implement JWT tokens
2. **Add User Profile** - View and update user information
3. **Add Password Reset** - Email-based password recovery
4. **Add Role-Based Access** - Admin vs Regular users
5. **Add Frontend** - React, Vue, or Angular integration

---

## 🎓 UNDERSTANDING MAVEN

### What is Maven?

Maven is a build automation and dependency management tool for Java projects.

### Key Concepts:

1. **pom.xml** - Project Object Model
   - Defines project dependencies
   - Configures build process
   - Specifies project metadata

2. **Dependencies** - External libraries
   - Spring Boot Web - REST API framework
   - Spring Data JPA - Database access
   - MySQL Connector - Database driver
   - Lombok - Reduces boilerplate code

3. **Maven Commands:**
   ```bash
   mvn clean          # Remove build files
   mvn compile        # Compile source code
   mvn test           # Run tests
   mvn package        # Create JAR file
   mvn install        # Install to local repository
   mvn spring-boot:run # Run Spring Boot app
   ```

---

## 📚 LEARNING RESOURCES

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Maven Tutorial:** https://maven.apache.org/guides/getting-started/
- **Spring Data JPA:** https://spring.io/projects/spring-data-jpa
- **RESTful API Design:** https://restfulapi.net/

---

## ✅ SUBMISSION CHECKLIST

Before submitting your project:

- [ ] GitHub repository created and named: IT342-Canadilla-WildcatsLounge
- [ ] All code committed and pushed to GitHub
- [ ] Application runs without errors
- [ ] User registration works correctly
- [ ] User login works correctly
- [ ] Passwords are hashed in database
- [ ] Duplicate email prevention works
- [ ] API responses follow correct format
- [ ] Clear commit messages documenting changes
- [ ] README.md created with project description

---

## 📞 SUPPORT

If you encounter any issues:
1. Check the error logs in the console
2. Verify all prerequisites are installed
3. Review the Common Issues section
4. Check database connectivity

---

**Good luck with your project! 🚀**

---

*Last Updated: March 7, 2026*
*Project: Wildcats Lounge - Phase 1*
*Developer: Canadilla*
