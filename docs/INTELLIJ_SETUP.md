# 🚀 IntelliJ IDEA Setup Guide for Wildcats Lounge

This guide will help you set up and run the Wildcats Lounge backend using IntelliJ IDEA.

---

## ✅ Prerequisites Checklist

Before starting, ensure you have:

- [ ] **Java 17** installed ([JAVA_SETUP.md](JAVA_SETUP.md) if needed)
- [ ] **IntelliJ IDEA** installed (Community or Ultimate Edition)
- [ ] **Supabase account** created ([SUPABASE_SETUP.md](SUPABASE_SETUP.md))
- [ ] **Supabase database** configured with connection details ready

---

## 📥 STEP 1: Download & Install IntelliJ IDEA

### Option 1: IntelliJ IDEA Community Edition (Free)

1. **Download:**
   - Visit: https://www.jetbrains.com/idea/download/
   - Select: **Community Edition** (Free)
   - Download for Windows

2. **Install:**
   - Run the installer
   - **Check these options during installation:**
     - ✅ Create Desktop Shortcut
     - ✅ Update PATH variable
     - ✅ Add "Open Folder as Project"
     - ✅ .java File Association
     - ✅ .kt File Association

3. **Launch IntelliJ IDEA**

### Option 2: IntelliJ IDEA Ultimate (Paid/Student License)

If you have a student email:
- Get free license: https://www.jetbrains.com/community/education/#students
- Ultimate Edition includes additional features (database tools, Spring Boot dashboard)

---

## 🔧 STEP 2: Configure IntelliJ IDEA

### 2.1 Set Java SDK

1. Open IntelliJ IDEA
2. Go to: **File → Project Structure** (or `Ctrl+Alt+Shift+S`)
3. Under **Project Settings → Project**:
   - **SDK**: Select Java 17 (if not listed, click **Add SDK → Download JDK** → Amazon Corretto 17)
   - **Language Level**: 17 - Sealed types, always-strict floating-point semantics

4. Click **Apply** → **OK**

### 2.2 Install Recommended Plugins

1. Go to: **File → Settings → Plugins** (or `Ctrl+Alt+S`)
2. Search and install:
   - ✅ **Maven** (usually pre-installed)
   - ✅ **Spring Boot** (usually pre-installed)
   - ✅ **Database Tools** (for viewing Supabase data - Ultimate only)
   - ✅ **Lombok** (required for the project)

3. Restart IntelliJ after installation

---

## 📂 STEP 3: Open Your Project in IntelliJ

### Method 1: Open Entire Workspace

1. **Open IntelliJ IDEA**
2. Click: **Open**
3. Navigate to: `Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge`
4. Click **OK**

IntelliJ will detect multiple modules (backend, web, docs, mobile).

### Method 2: Open Backend Only (Recommended)

1. **Open IntelliJ IDEA**
2. Click: **Open**
3. Navigate to: `Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\backend`
4. Click **OK**

This focuses only on the Spring Boot backend.

---

## 🔄 STEP 4: Let IntelliJ Import Maven Project

After opening the project:

1. **IntelliJ will detect `pom.xml`** and show a popup:
   ```
   Maven build scripts found
   Load Maven Project? [Load] [Cancel]
   ```

2. Click **Load** (or **Import Changes**)

3. **Wait for Maven to download dependencies** (bottom-right corner shows progress)
   - This may take 2-5 minutes on first run
   - IntelliJ downloads Maven automatically if not installed
   - All dependencies from `pom.xml` will be downloaded

4. **Maven Tool Window** appears on the right sidebar (or **View → Tool Windows → Maven**)

---

## ⚙️ STEP 5: Configure Database Connection

### 5.1 Update `application.properties`

1. In IntelliJ, navigate to:
   ```
   backend/src/main/resources/application.properties
   ```

2. The file should already have Supabase configuration like this:
   ```properties
   # Supabase Database Configuration
   spring.datasource.url=jdbc:postgresql://<YOUR_SUPABASE_HOST>:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=<YOUR_SUPABASE_PASSWORD>
   spring.datasource.driver-class-name=org.postgresql.Driver
   ```

3. **Replace placeholders with your actual Supabase details:**
   - Get these from [Supabase Dashboard](https://app.supabase.com) → Your Project → Settings → Database
   - **Host**: Something like `db.xxxxxxxxxxxxx.supabase.co`
   - **Password**: The password you set during Supabase project creation

### 5.2 Example Configuration

```properties
# Supabase Database Configuration
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YourSupabasePassword123
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## ▶️ STEP 6: Run the Backend in IntelliJ

### Method 1: Using IntelliJ Run Button (Easiest)

1. **Locate the main class:**
   ```
   backend/src/main/java/edu/cit/canadilla/wildcatslounge/WildcatsLoungeApplication.java
   ```

2. **Open the file** and you'll see a **green play button** (▶️) next to:
   ```java
   public class WildcatsLoungeApplication {
       public static void main(String[] args) {
   ```

3. **Click the green play button** → **Run 'WildcatsLoungeApplication'**

4. **Console output** appears at the bottom showing:
   ```
   Started WildcatsLoungeApplication in X.XXX seconds
   ```

### Method 2: Using Maven Tool Window

1. Open **Maven Tool Window** (right sidebar)
2. Expand: **wildcatslounge → Plugins → spring-boot**
3. Double-click: **spring-boot:run**

### Method 3: Using Run Configuration

1. Go to: **Run → Edit Configurations**
2. Click **+** → **Spring Boot**
3. Configure:
   - **Name**: WildcatsLounge Backend
   - **Main class**: `edu.cit.canadilla.wildcatslounge.WildcatsLoungeApplication`
   - **Use classpath of module**: wildcatslounge
4. Click **OK**
5. Click **Run** (green play button in toolbar)

---

## ✅ STEP 7: Verify Backend is Running

### Check Console Output

You should see:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.0)

INFO  WildcatsLoungeApplication - Starting WildcatsLoungeApplication
INFO  WildcatsLoungeApplication - Started WildcatsLoungeApplication in 3.456 seconds
INFO  Tomcat - Tomcat started on port 8080 (http)
```

### Test API Endpoints

**1. Health Check:**
```
http://localhost:8080/api/auth/health
```

Open in browser or use IntelliJ HTTP Client:

**2. Create Test HTTP File (Optional):**

Create `backend/test.http`:
```http
### Health Check
GET http://localhost:8080/api/auth/health

### Register User
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

### Login User
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Click the green play buttons next to each request to test.

---

## 🛠️ STEP 8: Using IntelliJ Features

### Hot Reload (Auto-Restart on Changes)

IntelliJ + Spring Boot DevTools enables automatic restart:

1. **Make code changes**
2. **Build project**: `Ctrl+F9` (or **Build → Build Project**)
3. **Application restarts automatically** with your changes

### View Database (Ultimate Edition Only)

1. Go to: **View → Tool Windows → Database**
2. Click **+** → **Data Source** → **PostgreSQL**
3. Enter Supabase connection details:
   - **Host**: Your Supabase host
   - **Port**: 5432
   - **Database**: postgres
   - **User**: postgres
   - **Password**: Your Supabase password
4. Click **Test Connection** → **OK**
5. Browse tables and data visually

### Debug Mode

1. Set **breakpoints** by clicking left margin of code editor (red dot appears)
2. Click **Debug** button (bug icon) instead of Run
3. Application stops at breakpoints, allowing you to inspect variables

### Maven Commands

Use **Maven Tool Window** (right sidebar):
- **Lifecycle → clean**: Clean build artifacts
- **Lifecycle → install**: Compile and package
- **Plugins → spring-boot:run**: Run application

---

## 🔥 Common IntelliJ Issues & Solutions

### Issue 1: "Cannot resolve symbol" errors in code

**Solution:**
1. **File → Invalidate Caches** → **Invalidate and Restart**
2. Wait for IntelliJ to re-index the project

### Issue 2: Maven dependencies not downloading

**Solution:**
1. Open **Maven Tool Window**
2. Click **Reload All Maven Projects** (refresh icon)
3. Or: **File → Settings → Build Tools → Maven** → Verify Maven home directory

### Issue 3: "Port 8080 already in use"

**Solution:**
1. Stop previous run: Click **Stop** button (red square) in Run window
2. Or change port in `application.properties`:
   ```properties
   server.port=8081
   ```

### Issue 4: Lombok not working (@Getter, @Setter errors)

**Solution:**
1. **File → Settings → Plugins** → Search "Lombok" → Install
2. **File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors**
3. Check: ✅ **Enable annotation processing**
4. Restart IntelliJ

### Issue 5: Java version mismatch

**Solution:**
1. **File → Project Structure → Project**
2. Set **SDK**: Java 17
3. Set **Language Level**: 17
4. **File → Project Structure → Modules** → Set **Language Level**: 17

---

## 📊 IntelliJ Project Structure Overview

```
backend/
├── src/
│   ├── main/
│   │   ├── java/edu/cit/canadilla/wildcatslounge/
│   │   │   ├── WildcatsLoungeApplication.java  ← Run this file
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java         ← REST endpoints
│   │   │   ├── service/
│   │   │   │   └── UserService.java            ← Business logic
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java         ← Database queries
│   │   │   ├── entity/
│   │   │   │   └── User.java                   ← Database table
│   │   │   ├── dto/
│   │   │   │   └── (Request/Response classes)
│   │   │   └── util/
│   │   │       └── PasswordUtil.java           ← Password encryption
│   │   └── resources/
│   │       └── application.properties          ← Configuration file
│   └── test/                                    ← Unit tests
├── pom.xml                                      ← Maven dependencies
└── target/                                      ← Compiled files (auto-generated)
```

---

## ✅ Setup Complete!

You can now:

- ✅ **Run backend**: Click green play button in `WildcatsLoungeApplication.java`
- ✅ **Test APIs**: Use browser or HTTP client (`test.http`)
- ✅ **Debug code**: Set breakpoints and click Debug
- ✅ **View logs**: Check Run window at bottom
- ✅ **Hot reload**: Make changes and build (`Ctrl+F9`)

---

## 🎯 Next Steps

1. **Start backend** in IntelliJ (as described above)
2. **Set up frontend**: Follow [FRONTEND_SETUP.md](FRONTEND_SETUP.md)
3. **Test registration/login**: Use the web interface or HTTP client
4. **Check Supabase Dashboard**: Verify users are being saved to database

---

## 📚 Additional Resources

- **IntelliJ IDEA Docs**: https://www.jetbrains.com/help/idea/
- **Spring Boot in IntelliJ**: https://www.jetbrains.com/help/idea/spring-boot.html
- **IntelliJ Keyboard Shortcuts**: https://www.jetbrains.com/help/idea/mastering-keyboard-shortcuts.html

**Recommended Shortcuts:**
- `Ctrl+F9`: Build project (trigger hot reload)
- `Shift+F10`: Run
- `Shift+F9`: Debug
- `Ctrl+Shift+F10`: Run current file
- `Alt+4`: Open Run window
- `Alt+5`: Open Debug window

---

*Guide created: March 7, 2026*
*For: IT342-Canadilla-WildcatsLounge*
*Backend: Spring Boot 3.5.0 + Supabase PostgreSQL*
