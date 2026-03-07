# 🔧 MAVEN SETUP GUIDE - Complete Installation Tutorial

## ⚠️ CRITICAL: Check Java Version First!

**Spring Boot 3.5.0 requires Java 17 or higher.**

### Check Your Java Version NOW:

```powershell
java -version
```

**❌ If you see Java 11 or lower (like this):**
```
openjdk version "11.0.12" 2021-07-20  ← TOO OLD!
```

**🛑 STOP! You MUST upgrade to Java 17 first.**
**📖 Follow this guide:** [JAVA_SETUP.md](JAVA_SETUP.md)

---

**✅ If you see Java 17 or higher (like this):**
```
openjdk version "17.0.10" 2024-01-16  ← GOOD!
```

**✓ Continue with Maven installation below.**

---

## ✅ Your Project Configuration (Already Correct!)

Your Wildcats Lounge project **already follows all requirements**:

```xml
Group ID:    edu.cit.canadilla     ✓ Follows format: edu.cit.lastname
Artifact ID: wildcatslounge        ✓ Follows format: appname (lowercase, no spaces)
Base Package: edu.cit.canadilla.wildcatslounge  ✓ Auto-generated correctly
Repository:  IT342-Canadilla-WildcatsLounge     ✓ Follows format: IT342-Lastname-AppName
Spring Boot: 3.5.0                 ✓ Version 3.5.x (requires Java 17+)
Build Tool:  Maven                 ✓ Required
Architecture: REST API             ✓ Required
```

**Your configuration is perfect!** Now let's install Maven.

---

## 📥 PART 1: MAVEN INSTALLATION (Step-by-Step)

### Prerequisites Checklist

- [x] **Java JDK 17+** installed and verified (`java -version` shows 17+)
- [ ] Maven (we'll install this now)

Expected output: `java version "17"` or higher

❌ **If Java is NOT installed:**
1. Download from: https://www.oracle.com/java/technologies/downloads/
2. Choose: **Windows x64 Installer**
3. Install and restart your terminal

---

## 🚀 STEP 1: Download Apache Maven

### Option A: Download from Official Website

1. **Visit:** https://maven.apache.org/download.cgi

2. **Download:** `Binary zip archive`
   - Look for: **apache-maven-3.9.6-bin.zip** (or latest version)
   - Click the link under "Binary zip archive"

3. **Save** the file to your Downloads folder

### Option B: Direct Link
Download: https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip

---

## 📂 STEP 2: Extract Maven

1. **Navigate to Downloads folder:**
   ```powershell
   cd $env:USERPROFILE\Downloads
   ```

2. **Extract the ZIP file:**
   
   **Method A - Using PowerShell:**
   ```powershell
   Expand-Archive -Path apache-maven-3.9.6-bin.zip -DestinationPath C:\
   ```

   **Method B - Using File Explorer:**
   - Right-click `apache-maven-3.9.6-bin.zip`
   - Choose "Extract All..."
   - Extract to: `C:\`

3. **Rename the folder (recommended):**
   ```powershell
   Rename-Item "C:\apache-maven-3.9.6" "C:\apache-maven"
   ```

Your Maven is now at: **`C:\apache-maven`**

---

## 🔧 STEP 3: Set Environment Variables

### Method A: Using PowerShell (Recommended)

**Run PowerShell as Administrator:**
1. Press `Win + X`
2. Select **"Windows PowerShell (Admin)"** or **"Terminal (Admin)"**

**Set MAVEN_HOME:**
```powershell
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\apache-maven", "Machine")
```

**Add Maven to PATH:**
```powershell
$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$newPath = $path + ";C:\apache-maven\bin"
[System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
```

**Verify commands ran successfully:**
```powershell
Write-Host "MAVEN_HOME: $env:MAVEN_HOME"
Write-Host "PATH updated with Maven bin directory"
```

### Method B: Using System Properties (GUI)

1. **Open System Properties:**
   - Press `Win + R`
   - Type: `sysdm.cpl`
   - Press **Enter**

2. **Go to Advanced tab:**
   - Click **"Environment Variables"** button

3. **Create MAVEN_HOME (System Variables):**
   - Under "System variables" section
   - Click **"New"**
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\apache-maven`
   - Click **OK**

4. **Update PATH variable:**
   - Under "System variables"
   - Find and select **"Path"**
   - Click **"Edit"**
   - Click **"New"**
   - Add: `C:\apache-maven\bin`
   - Click **OK**

5. **Click OK on all dialogs**

---

## ✅ STEP 4: Verify Maven Installation

**IMPORTANT:** Close and reopen your terminal/PowerShell after setting environment variables.

1. **Open a NEW PowerShell window**

2. **Check Maven version:**
   ```powershell
   mvn -version
   ```

**Expected Output:**
```
Apache Maven 3.9.6 (...)
Maven home: C:\apache-maven
Java version: 17.0.x, vendor: Oracle Corporation
Java home: C:\Program Files\Java\jdk-17
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64"
```

✅ **If you see this, Maven is successfully installed!**

❌ **If you get "command not found":**
- Restart your terminal
- Verify PATH was updated correctly
- Check `C:\apache-maven\bin` exists

---

## 🎯 STEP 5: Test Maven with Your Project

### Navigate to backend folder:
```powershell
cd Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\backend
```

### Verify pom.xml exists:
```powershell
Get-Content pom.xml | Select-String -Pattern "artifactId"
```

Should show: `<artifactId>wildcatslounge</artifactId>`

### Download dependencies (first time):
```powershell
mvn clean install
```

**This will:**
- Download all required libraries (Spring Boot, MySQL, etc.)
- Compile your Java code
- Create a runnable JAR file
- Take 2-5 minutes on first run

**Expected final output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45.678 s
[INFO] Finished at: 2026-03-07T10:30:00+08:00
```

### Run your backend:
```powershell
mvn spring-boot:run
```

**Expected output:**
```
========================================
  Wildcats Lounge API is running!
  Access at: http://localhost:8080
========================================
```

---

## 📋 MAVEN QUICK REFERENCE

### Essential Maven Commands

```powershell
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Create JAR file
mvn package

# Install dependencies and build
mvn clean install

# Skip tests (faster build)
mvn clean install -DskipTests

# Run Spring Boot application
mvn spring-boot:run

# Show dependency tree
mvn dependency:tree

# Update dependencies
mvn clean install -U
```

---

## 🎓 Understanding Your pom.xml

Your `backend/pom.xml` contains:

### Project Coordinates
```xml
<groupId>edu.cit.canadilla</groupId>
<artifactId>wildcatslounge</artifactId>
<version>1.0.0</version>
```

This follows the **required naming convention**:
- **Group ID:** `edu.cit.canadilla` (edu.cit.lastname format) ✓
- **Artifact ID:** `wildcatslounge` (appname format) ✓
- **Base Package:** Generated as `edu.cit.canadilla.wildcatslounge` ✓

### Dependencies Included
1. **spring-boot-starter-web** - REST API support
2. **spring-boot-starter-data-jpa** - Database access
3. **spring-boot-starter-validation** - Input validation
4. **mysql-connector-j** - MySQL driver
5. **spring-security-crypto** - Password encryption
6. **lombok** - Reduce boilerplate code

---

## 🔍 TROUBLESHOOTING

### Issue 1: "mvn: command not found"

**Cause:** Environment variables not set or terminal not restarted

**Solution:**
1. Close ALL terminal windows
2. Open a NEW PowerShell window
3. Run: `mvn -version`

If still not working:
```powershell
# Check if MAVEN_HOME is set
echo $env:MAVEN_HOME

# Check if Maven bin is in PATH
echo $env:PATH | Select-String "maven"
```

### Issue 2: "JAVA_HOME not set"

**Solution:**
```powershell
# Find Java installation
where.exe java

# Set JAVA_HOME (replace with your JDK path)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Machine")
```

### Issue 3: Maven builds fail

**Solution:**
```powershell
# Clear Maven cache
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository"

# Rebuild
cd backend
mvn clean install -U
```

### Issue 4: Slow download speeds

**Solution:** Maven is downloading dependencies from the internet for the first time. This is normal and only happens once.

---

## 🌐 MAVEN REPOSITORY LOCATION

Downloaded dependencies are stored in:
```
C:\Users\YourUsername\.m2\repository\
```

This is Maven's local cache. Once downloaded, dependencies are reused across projects.

---

## ✅ VERIFICATION CHECKLIST

Complete this checklist to ensure Maven is properly set up:

- [ ] Java JDK 17+ installed (`java -version`)
- [ ] Maven downloaded (apache-maven-3.9.6-bin.zip)
- [ ] Maven extracted to `C:\apache-maven`
- [ ] MAVEN_HOME environment variable set
- [ ] Maven bin added to PATH
- [ ] Terminal/PowerShell restarted
- [ ] `mvn -version` works without errors
- [ ] Navigated to `backend/` folder
- [ ] `mvn clean install` completes successfully
- [ ] `mvn spring-boot:run` starts the application

---

## 🎯 GITHUB REPOSITORY SETUP

Your repository follows the **correct naming format**:

```
Repository Name: IT342-Canadilla-WildcatsLounge
                 ↓       ↓          ↓
                IT342-Lastname-AppName
```

**Perfect! ✓**

### Create GitHub Repository:

1. **Go to:** https://github.com
2. **Click:** "New Repository"
3. **Repository name:** `IT342-Canadilla-WildcatsLounge`
4. **Description:** "Wildcats Lounge - User Management System"
5. **Public** or **Private** (as per your instructor's requirement)
6. **DO NOT** initialize with README, .gitignore, or license (you already have these)
7. **Click:** "Create repository"

### Push your code:

```powershell
# Navigate to project root
cd Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: User Registration and Login - Phase 1"

# Add remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/IT342-Canadilla-WildcatsLounge.git

# Push to GitHub
git branch -M main
git push -u origin main
```

---

## 📊 PROJECT STRUCTURE VERIFICATION

Your backend structure **matches requirements**:

```
backend/
├── src/main/java/
│   └── edu/cit/canadilla/wildcatslounge/  ← Base package (correct!)
│       ├── WildcatsLoungeApplication.java
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       └── util/
├── src/main/resources/
│   └── application.properties
└── pom.xml  ← Maven configuration
```

**All requirements met!** ✓

---

## 🎓 SUBMISSION CHECKLIST (IT342 Phase 1)

Before submitting:

### Backend Requirements
- [x] Repository: **IT342-Canadilla-WildcatsLounge** ✓
- [x] Group ID: **edu.cit.canadilla** ✓
- [x] Artifact ID: **wildcatslounge** ✓
- [x] Spring Boot: **3.5.0** ✓
- [x] Build Tool: **Maven** ✓
- [x] Architecture: **REST API** ✓

### Features Implemented
- [x] User Registration (name, email, password)
- [x] User Login (email, password authentication)
- [x] Password hashing (BCrypt)
- [x] Email validation
- [x] Duplicate email prevention
- [x] MySQL database integration
- [x] Input validation

### Documentation
- [x] README.md in repository
- [x] Clear commit messages
- [x] API endpoints documented

---

## 🚀 NEXT STEPS

1. ✅ **Install Maven** (follow steps above)
2. ✅ **Verify installation** (`mvn -version`)
3. ✅ **Build project** (`mvn clean install`)
4. ✅ **Run backend** (`mvn spring-boot:run`)
5. ✅ **Test API** (use test-api.http)
6. ✅ **Create GitHub repo**
7. ✅ **Push code to GitHub**
8. ✅ **Submit repository link**

---

## 📞 QUICK HELP

**Maven installation:**
```powershell
# Check Maven
mvn -version

# Check Java
java -version

# Build project
cd backend
mvn clean install

# Run project
mvn spring-boot:run
```

**If stuck, check:**
1. Java installed? (`java -version`)
2. Maven PATH set? (Restart terminal)
3. In backend folder? (`cd backend`)
4. Internet connection? (Maven needs to download dependencies)

---

## 📚 Additional Resources

- **Maven Official:** https://maven.apache.org/
- **Spring Boot:** https://spring.io/projects/spring-boot
- **Maven in 5 Minutes:** https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
- **Your Project Docs:** See `docs/` folder

---

**Maven setup complete! Your project configuration is already perfect! 🎉**

*Last Updated: March 7, 2026*
*For: IT342 - Project Phase 1*
