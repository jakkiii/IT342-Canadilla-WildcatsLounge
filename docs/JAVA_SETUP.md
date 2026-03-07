# ⚠️ IMPORTANT: Java 17 Required for Spring Boot 3.5

## Current Issue

Your system has **Java 11**, but Spring Boot 3.5.0 **requires Java 17 or higher**.

```
Current Java Version: 11.0.12  ❌
Required Java Version: 17+     ✅
```

---

## 🚀 Install Java 17 (Amazon Corretto)

### Option 1: Amazon Corretto 17 (Recommended - Production Ready)

**Step 1: Download Java 17**
1. Visit: https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html
2. Download: **Windows x64 MSI installer** (`amazon-corretto-17-x64-windows-jdk.msi`)

**Step 2: Install**
1. Run the downloaded `.msi` installer
2. Follow the installation wizard
3. **CHECK** "Set JAVA_HOME variable" (Important!)
4. **CHECK** "Add to PATH" (Important!)
5. Click "Install"

**Step 3: Verify Installation**

Open a **NEW PowerShell terminal** (close current one) and run:

```powershell
java -version
```

**Expected output:**
```
openjdk version "17.0.x" 2024-xx-xx
OpenJDK Runtime Environment Corretto-17.0.x.x (build 17.0.x+x)
OpenJDK 64-Bit Server VM Corretto-17.0.x.x (build 17.0.x+x, mixed mode, sharing)
```

---

### Option 2: Microsoft OpenJDK 17

**Step 1: Download**
1. Visit: https://learn.microsoft.com/en-us/java/openjdk/download
2. Select: **OpenJDK 17 LTS**
3. Download: **Windows x64 MSI**

**Step 2: Install**
1. Run the installer
2. Accept defaults
3. Ensure "Add to PATH" is checked

**Step 3: Verify**
```powershell
java -version
```

Should show version **17.x.x**

---

### Option 3: Oracle JDK 17

**Step 1: Download**
1. Visit: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. Download: **Windows x64 Installer**

**Step 2: Install**
1. Run installer
2. Follow prompts
3. Complete installation

**Step 3: Verify**
```powershell
java -version
```

---

## 🔧 Manual Environment Variable Setup (If Not Set Automatically)

If Java is installed but not recognized, set environment variables manually:

### Set JAVA_HOME

```powershell
# Run PowerShell as Administrator

# For Amazon Corretto 17
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Amazon Corretto\jdk17.0.10_7", "Machine")

# OR for Microsoft OpenJDK 17
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Microsoft\jdk-17.0.10.7-hotspot", "Machine")

# Verify
[System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
```

*(Adjust the path based on your actual installation path)*

### Add to PATH

```powershell
# Run PowerShell as Administrator

$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$javaPath = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine") + "\bin"
$newPath = $path + ";" + $javaPath
[System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
```

### Apply Changes

**Close all PowerShell/Terminal windows and open a NEW one.**

```powershell
java -version
javac -version
```

Both should show **Java 17**.

---

## 🧪 Verification Checklist

Run these commands in a **new terminal**:

```powershell
# Check Java version (should be 17+)
java -version

# Check Java compiler (should be 17+)
javac -version

# Check JAVA_HOME
echo $env:JAVA_HOME

# Verify JAVA_HOME points to bin folder
Test-Path "$env:JAVA_HOME\bin\java.exe"
```

**Expected results:**
```
✅ java -version      → 17.0.x
✅ javac -version     → 17.0.x
✅ JAVA_HOME          → C:\Program Files\...\jdk-17.x.x
✅ Test-Path          → True
```

---

## ⚠️ Common Issues

### Issue 1: Multiple Java Versions Installed

**Problem:** System still shows Java 11 after installing Java 17.

**Solution:**
1. Uninstall Java 11 from Control Panel → Programs
2. OR manually update PATH to prioritize Java 17:
   ```powershell
   # Remove old Java from PATH
   # Add Java 17 path at the beginning
   ```

### Issue 2: JAVA_HOME Not Set

**Problem:** `echo $env:JAVA_HOME` returns nothing.

**Solution:**
```powershell
# Run as Administrator
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Amazon Corretto\jdk17.0.10_7", "Machine")
```

Replace path with your actual Java 17 installation path.

### Issue 3: Command Not Found

**Problem:** `java -version` says "command not found"

**Solution:**
1. Restart terminal (important!)
2. Check PATH includes `%JAVA_HOME%\bin`
3. Reinstall Java with "Add to PATH" checked

---

## 🎯 Why Java 17?

Spring Boot 3.x requires **Java 17 minimum**:

| Spring Boot Version | Minimum Java Version |
|---------------------|---------------------|
| Spring Boot 2.x | Java 8+ |
| Spring Boot 3.x | **Java 17+** ✅ |

Your project uses **Spring Boot 3.5.0**, so **Java 17 is mandatory**.

---

## 📋 Next Steps After Installing Java 17

Once Java 17 is verified:

1. ✅ **Verify Java:** `java -version` shows 17+
2. ✅ **Install Maven:** Follow [MAVEN_SETUP.md](MAVEN_SETUP.md)
3. ✅ **Build Project:** `cd backend && mvn clean install`
4. ✅ **Run Backend:** `mvn spring-boot:run`

---

## 🔗 Quick Reference Links

- **Amazon Corretto 17:** https://aws.amazon.com/corretto/
- **Microsoft OpenJDK 17:** https://learn.microsoft.com/en-us/java/openjdk/download
- **Oracle JDK 17:** https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- **Spring Boot 3.x Requirements:** https://docs.spring.io/spring-boot/system-requirements.html

---

## 💡 Recommendation

**Use Amazon Corretto 17** - it's:
- ✅ Free and production-ready
- ✅ Long-term support (LTS)
- ✅ Optimized for performance
- ✅ Used by AWS in production
- ✅ No licensing concerns

---

*Guide created: March 7, 2026*
*Project: IT342-Canadilla-WildcatsLounge*
*Required for: Spring Boot 3.5.0 compatibility*
