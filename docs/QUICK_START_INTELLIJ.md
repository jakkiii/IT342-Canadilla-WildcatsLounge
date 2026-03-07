# 🚀 Quick Start Guide - IntelliJ + Supabase Workflow

**Get Wildcats Lounge running in 15 minutes!**

This guide streamlines the setup process for running the backend in IntelliJ IDEA with Supabase cloud database.

---

## ✅ Prerequisites Quick Check

Open PowerShell and run:

```powershell
# Check Java version
java -version
```

**✅ If you see Java 17+:** Continue below  
**❌ If you see Java 11 or older:** Install Java 17 first → [JAVA_SETUP.md](JAVA_SETUP.md)

---

## 📋 5-Step Setup Process

### STEP 1: Download IntelliJ IDEA (5 min)

1. **Visit:** https://www.jetbrains.com/idea/download/
2. **Download:** Community Edition (Free)
3. **Install:** Run installer with default settings
4. **Launch IntelliJ IDEA**

---

### STEP 2: Create Supabase Database (5 min)

1. **Visit:** https://supabase.com
2. **Sign up** with GitHub or email
3. **Create new project:**
   - Name: `wildcatslounge`
   - Database Password: **Create & SAVE this!** (e.g., `MyPassword123!`)
   - Region: Choose closest to you
   - Click: **Create new project**
4. **Wait 2-3 minutes** for provisioning

5. **Get connection details:**
   - Click: **Settings** (gear icon) → **Database**
   - Note down:
     - **Host:** `db.xxxxxxxxxxxxx.supabase.co`
     - **Password:** The password you just created

---

### STEP 3: Open Project in IntelliJ (2 min)

1. **Launch IntelliJ IDEA**
2. **Click:** Open
3. **Navigate to:** `Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\backend`
4. **Click:** OK
5. **Wait for IntelliJ to:**
   - Detect Maven project (popup appears)
   - Click: **Load** or **Import Changes**
   - Download dependencies (2-5 minutes first time)

---

### STEP 4: Configure Database Connection (2 min)

1. **In IntelliJ, open:**
   ```
   backend/src/main/resources/application.properties
   ```

2. **Find these lines:**
   ```properties
   spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres
   spring.datasource.password=YOUR_SUPABASE_PASSWORD
   ```

3. **Replace with your actual Supabase details:**
   ```properties
   spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
   spring.datasource.password=MyPassword123!
   ```
   *(Use YOUR actual host and password from Step 2)*

4. **Save file:** `Ctrl+S`

---

### STEP 5: Run Backend (1 min)

1. **In IntelliJ, open:**
   ```
   backend/src/main/java/edu/cit/canadilla/wildcatslounge/WildcatsLoungeApplication.java
   ```

2. **Click the green play button (▶️)** next to:
   ```java
   public class WildcatsLoungeApplication {
   ```

3. **Wait for backend to start** (console at bottom shows):
   ```
   Started WildcatsLoungeApplication in 3.456 seconds
   Tomcat started on port 8080 (http)
   ```

4. **Test it works:**
   - Open browser: http://localhost:8080/api/auth/health
   - Should see: `{"status":"OK"}`

---

## ✅ You're Done! Backend is Running

**What you just set up:**
- ✅ Java 17 (Spring Boot requirement)
- ✅ IntelliJ IDEA (auto-manages Maven)
- ✅ Supabase PostgreSQL database (cloud-hosted)
- ✅ Backend running on port 8080

---

## 🧪 Test the API

### Option 1: Use Browser

**Health check:**
```
http://localhost:8080/api/auth/health
```

### Option 2: Use IntelliJ HTTP Client

1. **Create file:** `backend/test.http`
2. **Add this content:**
   ```http
   ### Health Check
   GET http://localhost:8080/api/auth/health

   ### Register User
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json

   {
     "name": "Test User",
     "email": "test@example.com",
     "password": "password123"
   }

   ### Login User
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json

   {
     "email": "test@example.com",
     "password": "password123"
   }
   ```

3. **Click green play buttons** next to each request

---

## 🗄️ View Your Data in Supabase

1. **Go to:** https://app.supabase.com
2. **Select your project:** wildcatslounge
3. **Click:** Table Editor (left sidebar)
4. **See `users` table** (auto-created by backend)
5. **Register a user** (using HTTP request above)
6. **Refresh table** → See new user with encrypted password!

---

## 🎯 Next Steps

### Run Frontend (Optional)

Open new PowerShell terminal:
```powershell
cd Z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\web
npm install
npm run dev
```

Frontend runs at: http://localhost:3000

### Start Development

1. **Make code changes** in IntelliJ
2. **Build project:** `Ctrl+F9` (auto-reloads backend)
3. **Test changes** via HTTP client or frontend
4. **Check database** in Supabase Dashboard

---

## 🚨 Troubleshooting

### "Cannot connect to database"

**Check:**
1. Supabase project is active (visit dashboard, might be paused after inactivity)
2. Password in `application.properties` matches Supabase password
3. Internet connection is working

**Fix:**
- Go to Supabase Dashboard → Settings → Database → Reset password
- Update `application.properties` with new password
- Restart backend in IntelliJ

### "Port 8080 already in use"

**Fix:**
1. Click **Stop** button (red square) in IntelliJ Run window
2. Or change port in `application.properties`:
   ```properties
   server.port=8081
   ```

### "Maven dependencies not downloading"

**Fix:**
1. IntelliJ → **View → Tool Windows → Maven**
2. Click **Reload All Maven Projects** (refresh icon)
3. Wait for download to complete

### Lombok errors (@Getter, @Setter red underlines)

**Fix:**
1. **File → Settings → Plugins**
2. Search: **Lombok**
3. Click: **Install**
4. **Restart IntelliJ**

---

## 📚 Detailed Guides

If you need more help:
- **[JAVA_SETUP.md](JAVA_SETUP.md)** - Install Java 17
- **[SUPABASE_SETUP.md](SUPABASE_SETUP.md)** - Detailed Supabase setup
- **[INTELLIJ_SETUP.md](INTELLIJ_SETUP.md)** - Complete IntelliJ guide
- **[FRONTEND_SETUP.md](FRONTEND_SETUP.md)** - Frontend setup

---

## ✅ Checklist

Before submitting to instructor:

- [ ] Backend runs without errors in IntelliJ
- [ ] Can register a user via API
- [ ] Can login with registered user
- [ ] Users saved to Supabase (check Table Editor)
- [ ] Code pushed to GitHub: `IT342-Canadilla-WildcatsLounge`

---

## 🎓 Summary

**What you have:**
- Spring Boot 3.5.0 backend (Java 17)
- Cloud PostgreSQL database (Supabase)
- IntelliJ IDEA managing Maven automatically
- REST API with registration & login
- BCrypt password encryption

**No manual installation needed for:**
- ❌ MySQL (using Supabase)
- ❌ Maven (IntelliJ handles it)
- ❌ Database setup scripts (auto-created)

**You're ready for Phase 1 submission!** 🎉

---

*Quick Start Guide - March 7, 2026*  
*Total Setup Time: ~15 minutes*  
*For: IT342-Canadilla-WildcatsLounge*
