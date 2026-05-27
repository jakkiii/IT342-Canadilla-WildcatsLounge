# Run the Wildcats Lounge backend with a fresh Maven build.
# Use this instead of IntelliJ Run if /api/menu or other routes return 404.
#
# Prerequisites: backend/.env configured with your Supabase credentials.

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$envFile = Join-Path $PSScriptRoot ".env"
if (-not (Test-Path $envFile)) {
    Write-Host "ERROR: backend/.env not found. Copy .env.example to .env and fill in Supabase credentials." -ForegroundColor Red
    exit 1
}
$envContent = Get-Content $envFile -Raw
if ($envContent -match "YOUR_PROJECT_REF") {
    Write-Host @"

ERROR: backend/.env still has placeholder YOUR_PROJECT_REF in DB_URL.

Fix:
  1. Supabase Dashboard -> Settings -> Database
  2. Copy your host (db.xxxxx.supabase.co)
  3. Update DB_URL in backend/.env
  4. Run this script again

"@ -ForegroundColor Red
    exit 1
}

# Stop whatever is on 8080 (often an old IntelliJ run without staff routes)
$on8080 = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($on8080) {
    $pid8080 = $on8080[0].OwningProcess
    Write-Host "Stopping process $pid8080 on port 8080 (old backend)..." -ForegroundColor Yellow
    Stop-Process -Id $pid8080 -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

Write-Host "`nClean build + start on http://localhost:8080 ...`n" -ForegroundColor Cyan
Write-Host "After start, verify: curl http://localhost:8080/api/auth/staff/orders" -ForegroundColor Gray
Write-Host "  -> should say Unauthorized (401), NOT 404`n" -ForegroundColor Gray
mvn clean spring-boot:run
