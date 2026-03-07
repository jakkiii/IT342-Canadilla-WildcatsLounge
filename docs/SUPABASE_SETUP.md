# 🗄️ Supabase Database Setup Guide

This guide will help you create and configure a Supabase PostgreSQL database for the Wildcats Lounge backend.

---

## 🌟 What is Supabase?

**Supabase** is an open-source Firebase alternative providing:
- ✅ **PostgreSQL Database** (Cloud-hosted, no local installation needed)
- ✅ **Auto-generated REST APIs**
- ✅ **Real-time subscriptions**
- ✅ **Dashboard for viewing data**
- ✅ **Free tier** (Perfect for development and small projects)

**Why Supabase for this project?**
- No need to install MySQL locally
- Easy to monitor database via web dashboard
- Production-ready PostgreSQL database
- Free hosting with generous limits

---

## 📥 STEP 1: Create Supabase Account

1. **Visit Supabase:**
   - Go to: https://supabase.com
   - Click: **Start your project**

2. **Sign Up:**
   - Use **GitHub account** (recommended) or email
   - Complete registration

3. **Verify your email** (if using email signup)

---

## 🚀 STEP 2: Create New Project

1. **Dashboard:**
   - After login, you'll see: https://app.supabase.com
   - Click: **New Project**

2. **Project Settings:**
   ```
   Organization: Choose or create new
   Name: wildcatslounge
   Database Password: [Create a strong password - SAVE THIS!]
   Region: Choose closest to you (e.g., Southeast Asia, US West, etc.)
   Pricing Plan: Free
   ```

3. **Click: Create new project**

4. **Wait 2-3 minutes** for database provisioning

---

## 🔑 STEP 3: Get Database Connection Details

### 3.1 Navigate to Settings

1. In your project dashboard, click: **Settings** (gear icon on left sidebar)
2. Click: **Database**

### 3.2 Find Connection Info

Scroll to **Connection Info** section:

```
Host: db.xxxxxxxxxxxxx.supabase.co
Database name: postgres
Port: 5432
User: postgres
Password: [The password you set in Step 2]
```

### 3.3 Copy Connection String

Find **Connection string → Java** tab:

```
jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres?user=postgres&password=YOUR_PASSWORD
```

**⚠️ IMPORTANT: Save these details! You'll need them for backend configuration.**

---

## 📝 STEP 4: Configure Backend for Supabase

### 4.1 Update `application.properties`

1. Open IntelliJ IDEA
2. Navigate to: `backend/src/main/resources/application.properties`
3. Update with your Supabase connection details:

```properties
# Application Configuration
spring.application.name=WildcatsLounge

# Server Configuration
server.port=8080

# Supabase PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_supabase_password_here
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool Settings (Optional - for better performance)
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000

# Enable detailed error messages
server.error.include-message=always
server.error.include-binding-errors=always
```

### 4.2 Replace Placeholders

**Find and replace these in the code above:**
- `db.xxxxxxxxxxxxx.supabase.co` → Your actual Supabase host
- `your_supabase_password_here` → Your actual database password

### 4.3 Example (Real Configuration)

```properties
# Example with actual values
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=MyStrongPassword123!
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## ✅ STEP 5: Verify Database Connection

### 5.1 Run Backend in IntelliJ

1. Open: `WildcatsLoungeApplication.java`
2. Click the **green play button** (▶️)
3. Check console output for:
   ```
   INFO  HikariDataSource - HikariPool-1 - Starting...
   INFO  HikariDataSource - HikariPool-1 - Start completed.
   INFO  WildcatsLoungeApplication - Started WildcatsLoungeApplication
   ```

### 5.2 Check for Errors

**❌ If you see connection errors:**
```
HikariPool-1 - Exception during pool initialization
Connection refused
```

**Solutions:**
1. Verify `application.properties` has correct host, password
2. Check Supabase project is **Active** (not paused - happens after inactivity on free tier)
3. Ensure no typos in connection string
4. Restart IntelliJ and try again

**✅ If no errors:** Database connection successful!

---

## 🗂️ STEP 6: View Your Data in Supabase Dashboard

### 6.1 Access Table Editor

1. Go to: https://app.supabase.com
2. Select your project: **wildcatslounge**
3. Click: **Table Editor** (left sidebar)

### 6.2 View `users` Table

After running the backend:
1. The `users` table is **auto-created** (thanks to `spring.jpa.hibernate.ddl-auto=update`)
2. You'll see columns:
   ```
   - id (bigint, primary key)
   - name (varchar)
   - email (varchar, unique)
   - password (varchar, encrypted)
   - created_at (timestamp)
   - updated_at (timestamp)
   ```

### 6.3 Test Registration

1. **Register a user** via API:
   ```http
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json

   {
     "name": "Test User",
     "email": "test@example.com",
     "password": "password123"
   }
   ```

2. **Refresh Supabase Table Editor**
3. **See new row** in `users` table with:
   - Encrypted password (BCrypt hash)
   - Timestamp for `created_at`

---

## 🔐 STEP 7: Secure Your Credentials

### ⚠️ NEVER commit passwords to GitHub!

### Option 1: Use Environment Variables (Recommended)

**1. Update `application.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://${SUPABASE_HOST}:5432/postgres
spring.datasource.username=${SUPABASE_USER}
spring.datasource.password=${SUPABASE_PASSWORD}
```

**2. Set environment variables in IntelliJ:**
- **Run → Edit Configurations**
- Find your run configuration
- **Environment Variables** field:
  ```
  SUPABASE_HOST=db.xxxxx.supabase.co;SUPABASE_USER=postgres;SUPABASE_PASSWORD=YourPassword123
  ```

### Option 2: Use `.env` file (for local development)

**1. Create `backend/.env`:**
```env
SUPABASE_HOST=db.xxxxx.supabase.co
SUPABASE_USER=postgres
SUPABASE_PASSWORD=YourPassword123
```

**2. Add to `.gitignore`:**
```
.env
*.env
application-local.properties
```

**3. Load in application:**
Use Spring Boot profiles or a library like `spring-dotenv`.

---

## 📊 Supabase Dashboard Features

### Table Editor
- View all tables and data
- Add/edit/delete rows manually
- Run SQL queries

### SQL Editor
- Write custom SQL queries
- Create indexes, views
- Example:
  ```sql
  SELECT * FROM users WHERE email LIKE '%@example.com';
  ```

### Database → Roles
- Manage database users and permissions

### Database → Extensions
- Enable PostgreSQL extensions (e.g., `uuid-ossp`, `pg_trgm`)

### Logs
- View database logs
- Monitor queries
- Debug connection issues

---

## 🚀 Advanced: Connection Pooling

For better performance, configure HikariCP (auto-included in Spring Boot):

```properties
# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

---

## 🛠️ Troubleshooting

### Error: "FATAL: password authentication failed"

**Solution:**
- Double-check password in `application.properties`
- Reset password in Supabase Dashboard → Settings → Database → Reset password

### Error: "Connection timed out"

**Solution:**
- Check internet connection
- Verify Supabase project is active (not paused)
- Ensure firewall/antivirus isn't blocking port 5432

### Error: "relation 'users' does not exist"

**Solution:**
- Backend hasn't created tables yet
- Run backend once: It will auto-create tables
- Check `spring.jpa.hibernate.ddl-auto=update` is set

### Project Paused (Free Tier)

Free tier projects pause after **1 week of inactivity**.

**Solution:**
- Visit Supabase Dashboard
- Click **Restore** or **Resume** button
- Wait 1-2 minutes for database to wake up

---

## 📋 Configuration Summary

**What you configured:**
- ✅ Supabase project created
- ✅ Database connection details retrieved
- ✅ `application.properties` updated with PostgreSQL driver
- ✅ Backend connects to Supabase successfully
- ✅ Tables auto-created on backend startup

**What happens automatically:**
- ✅ Spring Boot creates `users` table in Supabase
- ✅ Hibernate manages schema updates
- ✅ Data persists in cloud (accessible from Supabase Dashboard)

---

## ✅ Setup Complete!

You now have:
- ✅ Cloud-hosted PostgreSQL database (Supabase)
- ✅ Backend connected to Supabase
- ✅ Dashboard to view/manage data
- ✅ No local database installation needed!

**Next Steps:**
1. **Run backend** in IntelliJ: [INTELLIJ_SETUP.md](INTELLIJ_SETUP.md)
2. **Test API endpoints**: Register/Login users
3. **View data** in Supabase Table Editor

---

## 🔗 Resources

- **Supabase Docs**: https://supabase.com/docs
- **Supabase Dashboard**: https://app.supabase.com
- **Java PostgreSQL Driver**: https://jdbc.postgresql.org/documentation/
- **Spring Boot + PostgreSQL**: https://spring.io/guides/gs/accessing-data-jpa/

---

*Guide created: March 7, 2026*
*For: IT342-Canadilla-WildcatsLounge*
*Database: Supabase PostgreSQL (Free Tier)*
