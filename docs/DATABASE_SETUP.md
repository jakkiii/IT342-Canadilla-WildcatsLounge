# 🗄️ MYSQL DATABASE SETUP GUIDE - Wildcats Lounge

## Complete Database Setup Tutorial

This guide will walk you through setting up MySQL database for the Wildcats Lounge application.

---

## 📥 STEP 1: Install MySQL

### Download MySQL
1. Visit: **https://dev.mysql.com/downloads/installer/**
2. Download **MySQL Installer for Windows**
3. Choose the **mysql-installer-community** version

### Installation Process

1. **Run the installer** (mysql-installer-community-x.x.x.msi)

2. **Choose Setup Type:**
   - Select **"Developer Default"**
   - This installs MySQL Server, Workbench, and other useful tools
   - Click **Next**

3. **Check Requirements:**
   - Installer will check for required software
   - Click **Execute** to install missing requirements
   - Wait for completion, then click **Next**

4. **Installation:**
   - Click **Execute** to begin installation
   - Wait for all products to install (may take 5-10 minutes)
   - Click **Next** when complete

5. **Product Configuration:**
   - Click **Next** to start configuring MySQL Server

---

## ⚙️ STEP 2: Configure MySQL Server

### Type and Networking

1. **Config Type:**
   - Select **Development Computer**
   - Click **Next**

2. **Authentication Method:**
   - Select **Use Strong Password Encryption**
   - Click **Next**

3. **Accounts and Roles:**
   - **Root Password**: Leave EMPTY (for development)
     - Or set a password and remember it!
   - Click **Next**

4. **Windows Service:**
   - Keep **Configure MySQL Server as a Windows Service** checked
   - Service Name: **MySQL80** (default)
   - ✓ Check **Start the MySQL Server at System Startup**
   - Click **Next**

5. **Apply Configuration:**
   - Click **Execute**
   - Wait for all steps to complete (green checkmarks)
   - Click **Finish**

6. **Product Configuration:**
   - Click **Next** through remaining products
   - Click **Finish** to complete installation

---

## ✅ STEP 3: Verify MySQL Installation

### Option 1: Using MySQL Workbench (GUI)

1. **Open MySQL Workbench**
   - Search for "MySQL Workbench" in Windows Start menu
   - Launch the application

2. **Connect to Database:**
   - You should see **Local instance MySQL80**
   - Click on it to connect
   - If you set a password, enter it
   - Click **OK**

3. **Verify Connection:**
   - You should see the SQL editor
   - Success! MySQL is running

### Option 2: Using Command Line

1. **Open PowerShell**

2. **Navigate to MySQL bin directory:**
   ```powershell
   cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
   ```

3. **Login to MySQL:**
   ```powershell
   # If no password:
   .\mysql -u root

   # If you set a password:
   .\mysql -u root -p
   # Then enter your password
   ```

4. **Check MySQL version:**
   ```sql
   SELECT VERSION();
   ```

5. **Exit MySQL:**
   ```sql
   exit;
   ```

---

## 🎯 STEP 4: Verify MySQL Service is Running

### Using Windows Services

1. Press `Win + R`
2. Type `services.msc` and press Enter
3. Look for **MySQL80** in the list
4. Status should be **Running**
5. Startup Type should be **Automatic**

### Using PowerShell

```powershell
Get-Service -Name MySQL80
```

Expected output:
```
Status   Name               DisplayName
------   ----               -----------
Running  MySQL80            MySQL80
```

---

## 🔧 STEP 5: Configure Application Database

The Wildcats Lounge application is configured to **automatically create the database** on first run.

### Database Configuration (Already Set)

Your `application.properties` is already configured:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wildcatslounge_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

### If You Set a MySQL Password

1. **Open:** `src/main/resources/application.properties`

2. **Update the password line:**
   ```properties
   spring.datasource.password=YOUR_PASSWORD_HERE
   ```

3. **Save the file**

---

## 🚀 STEP 6: First Run - Database Auto-Creation

### Start Backend Application

```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
mvn spring-boot:run
```

### What Happens Automatically

1. **Database Creation:**
   - Spring Boot connects to MySQL
   - Creates database: `wildcatslounge_db`
   - Creates table: `users`

2. **Table Structure:**
   ```sql
   CREATE TABLE users (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       email VARCHAR(100) NOT NULL UNIQUE,
       password VARCHAR(255) NOT NULL,
       created_at DATETIME NOT NULL,
       updated_at DATETIME
   );
   ```

3. **Console Output:**
   You'll see Hibernate SQL statements in the console:
   ```
   Hibernate: create table users (...)
   Hibernate: create index ...
   ```

---

## 📊 STEP 7: Verify Database Creation

### Using MySQL Workbench

1. **Open MySQL Workbench**
2. **Connect to Local instance**
3. **Refresh Schemas:**
   - Click the refresh icon in the Schemas panel
4. **Expand `wildcatslounge_db`**
5. **Expand `Tables`**
6. **You should see: `users` table**

### Using SQL Commands

```sql
-- Show all databases
SHOW DATABASES;

-- Use the wildcats lounge database
USE wildcatslounge_db;

-- Show all tables
SHOW TABLES;

-- View table structure
DESCRIBE users;

-- View all users (initially empty)
SELECT * FROM users;
```

---

## 📝 STEP 8: Manual Database Creation (Optional)

If you prefer to create the database manually:

### Using MySQL Workbench

1. **Open MySQL Workbench**
2. **Connect to server**
3. **Click** the "Create new schema" icon (cylinder with +)
4. **Schema Name:** `wildcatslounge_db`
5. **Charset:** `utf8mb4`
6. **Collation:** `utf8mb4_unicode_ci`
7. **Click Apply** → **Apply** → **Finish**

### Using SQL Command

```sql
CREATE DATABASE IF NOT EXISTS wildcatslounge_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE wildcatslounge_db;
```

---

## 🧪 STEP 9: Test Database Connection

### Create Test User via API

```powershell
$registerData = @{
    name = "Test User"
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method POST `
    -Body $registerData `
    -ContentType "application/json"
```

### Verify in Database

```sql
USE wildcatslounge_db;

SELECT id, name, email, created_at 
FROM users 
WHERE email = 'test@example.com';
```

You should see your test user with an encrypted password!

---

## 🔍 STEP 10: Useful MySQL Commands

### View User Data

```sql
-- Select all users
SELECT * FROM users;

-- Select specific fields
SELECT id, name, email, created_at FROM users;

-- Count total users
SELECT COUNT(*) as total_users FROM users;

-- Find user by email
SELECT * FROM users WHERE email = 'maria@example.com';

-- Get recently registered users
SELECT * FROM users ORDER BY created_at DESC LIMIT 5;
```

### Delete Test Data

```sql
-- Delete specific user
DELETE FROM users WHERE email = 'test@example.com';

-- Delete all users (be careful!)
DELETE FROM users;

-- Reset auto-increment
ALTER TABLE users AUTO_INCREMENT = 1;
```

---

## 🐛 TROUBLESHOOTING

### Issue 1: Can't connect to MySQL

**Solution:**
```powershell
# Check if MySQL service is running
Get-Service -Name MySQL80

# Start MySQL service if stopped
Start-Service -Name MySQL80
```

### Issue 2: "Access denied for user 'root'"

**Solution:**
- Verify your password in `application.properties`
- Reset MySQL root password using MySQL installer

### Issue 3: Port 3306 already in use

**Solution:**
```sql
-- Check MySQL port
SHOW VARIABLES LIKE 'port';

-- Change port in application.properties
spring.datasource.url=jdbc:mysql://localhost:3307/wildcatslounge_db?createDatabaseIfNotExist=true
```

### Issue 4: Database not created automatically

**Solution:**
1. Check MySQL is running
2. Verify JDBC URL has `?createDatabaseIfNotExist=true`
3. Check application logs for errors
4. Create database manually (see Step 8)

---

## 📈 Advanced: Backup and Restore

### Backup Database

```powershell
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

.\mysqldump -u root wildcatslounge_db > backup.sql
```

### Restore Database

```powershell
.\mysql -u root wildcatslounge_db < backup.sql
```

---

## ✅ Database Setup Checklist

- [x] MySQL Server installed
- [x] MySQL service running
- [x] Can connect via Workbench or CLI
- [x] Database `wildcatslounge_db` exists
- [x] Table `users` created
- [x] Application connects successfully
- [x] Can register and login users
- [x] Data appears in database

---

## 🎓 Next Steps

After database setup:
1. ✅ Run backend: `mvn spring-boot:run`
2. ✅ Run frontend: `npm run dev` (in frontend folder)
3. ✅ Test registration at http://localhost:3000/register
4. ✅ Verify data in MySQL Workbench

---

**Database setup complete! Your Wildcats Lounge application is ready to store data! 🎉**

---

*Last Updated: March 7, 2026*
*Project: Wildcats Lounge - Database Setup Guide*
