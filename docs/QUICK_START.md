# 🚀 QUICK START GUIDE - Wildcats Lounge

## Prerequisites Installation

### 1. Install Java JDK 17
1. Visit: https://www.oracle.com/java/technologies/downloads/
2. Download Windows x64 Installer
3. Run installer and follow prompts
4. Verify: Open PowerShell and run `java -version`

### 2. Install Apache Maven
1. Visit: https://maven.apache.org/download.cgi
2. Download Binary zip archive (apache-maven-3.x.x-bin.zip)
3. Extract to `C:\Program Files\Apache\maven`
4. Add to System PATH:
   - Press `Win + R`, type `sysdm.cpl`, press Enter
   - Go to "Advanced" tab → "Environment Variables"
   - Under "System variables", find "Path" → Click "Edit"
   - Click "New" → Add: `C:\Program Files\Apache\maven\bin`
   - Click OK on all dialogs
5. **Restart PowerShell/Terminal**
6. Verify: `mvn -version`

### 3. Install MySQL
1. Visit: https://dev.mysql.com/downloads/installer/
2. Download MySQL Installer
3. Choose "Developer Default" setup
4. Set root password (you can leave it empty for local development)
5. Complete installation
6. Verify MySQL is running (MySQL Workbench should open)

### 4. Install VS Code (Recommended)
1. Visit: https://code.visualstudio.com/
2. Download and install
3. Install Extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - REST Client

---

## Project Setup Steps

### Step 1: Open Project in VS Code
```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
code .
```

### Step 2: Update Database Configuration (if needed)
Open `src/main/resources/application.properties`

If you set a MySQL password, update:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3: Install Dependencies
Open Terminal in VS Code (`Ctrl + ~`) and run:
```powershell
mvn clean install
```

Wait for "BUILD SUCCESS" message (may take 2-5 minutes first time)

### Step 4: Run the Application
```powershell
mvn spring-boot:run
```

Wait for:
```
========================================
  Wildcats Lounge API is running!
  Access at: http://localhost:8080
========================================
```

---

## Testing the API

### Option 1: Using VS Code REST Client

1. Open `test-api.http` file
2. Click "Send Request" above each test case
3. View response in the split panel

### Option 2: Using PowerShell

**Register a user:**
```powershell
$registerData = @{
    name = "Test User"
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerData -ContentType "application/json"
```

**Login:**
```powershell
$loginData = @{
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
```

---

## Verify Database

1. Open MySQL Workbench
2. Connect to Local instance
3. Run these queries:
```sql
USE wildcatslounge_db;
SELECT * FROM users;
```

You should see your registered users with encrypted passwords.

---

## Common Commands

### Run Application
```powershell
mvn spring-boot:run
```

### Stop Application
Press `Ctrl + C` in the terminal

### Rebuild Project
```powershell
mvn clean install
```

### Create JAR file
```powershell
mvn clean package
```

---

## Project File Structure

```
IT342-Canadilla-WildcatsLounge/
├── src/main/
│   ├── java/edu/cit/canadilla/wildcatslounge/
│   │   ├── WildcatsLoungeApplication.java (Main)
│   │   ├── controller/AuthController.java (API Endpoints)
│   │   ├── service/UserService.java (Business Logic)
│   │   ├── repository/UserRepository.java (Database)
│   │   ├── entity/User.java (Database Table)
│   │   ├── dto/ (Request/Response Objects)
│   │   └── util/PasswordUtil.java (Password Hashing)
│   └── resources/
│       └── application.properties (Configuration)
├── pom.xml (Maven Dependencies)
├── README.md (Project Overview)
├── TUTORIAL.md (Detailed Guide)
└── test-api.http (API Tests)
```

---

## GitHub Setup

### Step 1: Initialize Git Repository
```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
git init
git add .
git commit -m "Initial commit: User Registration and Login implementation"
```

### Step 2: Create GitHub Repository
1. Go to https://github.com
2. Click "New Repository"
3. Name: `IT342-Canadilla-WildcatsLounge`
4. Description: "Wildcats Lounge - User Management System"
5. **DO NOT** initialize with README (we already have one)
6. Click "Create repository"

### Step 3: Push to GitHub
```powershell
git remote add origin https://github.com/YOUR_USERNAME/IT342-Canadilla-WildcatsLounge.git
git branch -M main
git push -u origin main
```

Replace `YOUR_USERNAME` with your GitHub username.

---

## Troubleshooting

### Maven not recognized
- Restart your terminal after installing Maven
- Verify PATH includes Maven bin directory

### Port 8080 in use
Change port in `application.properties`:
```properties
server.port=8081
```

### MySQL connection error
- Verify MySQL service is running
- Check username/password in application.properties
- Ensure MySQL is on port 3306

### Build fails
```powershell
mvn clean
mvn install -U
```

---

## What You've Built

✅ **User Registration System**
- Validates name, email, password
- Prevents duplicate emails
- Encrypts passwords with BCrypt

✅ **User Login System**
- Authenticates with email/password
- Secure password verification
- Returns user information on success

✅ **RESTful API**
- `/api/auth/register` - Create account
- `/api/auth/login` - User login
- `/api/auth/health` - Check API status

✅ **Database Integration**
- MySQL database
- Automatic table creation
- Secure data storage

---

## Next Steps After Phase 1

1. Add JWT token authentication
2. Create frontend (HTML/CSS/JavaScript)
3. Add password reset functionality
4. Implement user profile management
5. Add role-based access control

---

## Need More Help?

📖 **Read the detailed tutorial:** `TUTORIAL.md`
📝 **Review the code:** All files are well-commented
🧪 **Test the API:** Use `test-api.http`

---

**You're all set! Start your application and test it out! 🎉**
