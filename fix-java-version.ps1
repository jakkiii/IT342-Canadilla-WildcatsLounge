# ===================================================
# Fix Java Version - Set Java 19 as Default
# ===================================================
# This script must be run as ADMINISTRATOR

Write-Host "`n╔═══════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Setting Java 19 as Default Java        ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Java 19 installation path
$javaHome = "C:\Program Files\Java\jdk-19"

# Verify Java 19 exists
if (-not (Test-Path $javaHome)) {
    Write-Host "❌ ERROR: Java 19 not found at $javaHome" -ForegroundColor Red
    Write-Host "Please install Java 17 or higher first." -ForegroundColor Yellow
    pause
    exit
}

Write-Host "✅ Found Java 19 at: $javaHome" -ForegroundColor Green

# Set JAVA_HOME
Write-Host "`n📝 Setting JAVA_HOME..." -ForegroundColor Yellow
try {
    [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
    Write-Host "✅ JAVA_HOME = $javaHome" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to set JAVA_HOME: $_" -ForegroundColor Red
    Write-Host "Make sure you're running PowerShell as Administrator!" -ForegroundColor Yellow
    pause
    exit
}

# Update PATH
Write-Host "`n📝 Updating PATH..." -ForegroundColor Yellow
try {
    $path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    $javaBin = "$javaHome\bin"
    
    # Remove old Java paths from PATH
    Write-Host "Removing old Java 11 from PATH..." -ForegroundColor Yellow
    $pathArray = $path -split ";" | Where-Object { 
        $_ -notlike "*jdk-11*" -and 
        $_ -notlike "*Microsoft\jdk*" 
    }
    
    # Add Java 19 at the beginning (highest priority)
    if ($pathArray -notcontains $javaBin) {
        $newPath = $javaBin + ";" + ($pathArray -join ";")
        [System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "✅ Added Java 19 to PATH (highest priority)" -ForegroundColor Green
    } else {
        Write-Host "✅ Java 19 already in PATH" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Failed to update PATH: $_" -ForegroundColor Red
    pause
    exit
}

Write-Host "`n╔═══════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║           ✅ SUCCESS!                     ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════╝`n" -ForegroundColor Green

Write-Host "Environment variables updated:" -ForegroundColor Cyan
Write-Host "  JAVA_HOME = $javaHome" -ForegroundColor White
Write-Host "  PATH      = Java 19 added (priority)`n" -ForegroundColor White

Write-Host "⚠️  IMPORTANT NEXT STEPS:" -ForegroundColor Yellow
Write-Host "1. Close ALL PowerShell/Command Prompt/Terminal windows" -ForegroundColor White
Write-Host "2. Close VS Code (if open)" -ForegroundColor White
Write-Host "3. Open a NEW PowerShell window" -ForegroundColor White
Write-Host "4. Run: java -version" -ForegroundColor White
Write-Host "5. Should now show: java version `"19.0.2`"`n" -ForegroundColor White

Write-Host "Press any key to exit..." -ForegroundColor Cyan
pause
