[CmdletBinding()]
param(
    [string]$JavaHome
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$root = Split-Path -Parent $PSScriptRoot
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$artifactDir = Join-Path $root "artifacts\ci-runs\$timestamp"
New-Item -ItemType Directory -Path $artifactDir -Force | Out-Null

function Write-Section {
    param([string]$Text)
    Write-Host "`n=== $Text ===" -ForegroundColor Cyan
}

function Resolve-JavaHome {
    param([string]$PreferredJavaHome)

    if ($PreferredJavaHome) {
        if (Test-Path $PreferredJavaHome) {
            return $PreferredJavaHome
        }
        throw "JAVA_HOME path not found: $PreferredJavaHome"
    }

    $candidates = @(
        'C:\Program Files\Java\jdk-17',
        "$env:USERPROFILE\.jdks\ms-17.0.18"
    )

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    throw 'No supported JDK 17 installation found. Pass -JavaHome to the script.'
}

$javaHome = Resolve-JavaHome -PreferredJavaHome $JavaHome
$javaBin = Join-Path $javaHome 'bin'

$runInfoPath = Join-Path $artifactDir 'run-info.txt'
$summaryJsonPath = Join-Path $artifactDir 'summary.json'
$summaryMdPath = Join-Path $artifactDir 'summary.md'
$backendLog = Join-Path $artifactDir 'backend-test.log'
$webLog = Join-Path $artifactDir 'web-build.log'
$mobileLog = Join-Path $artifactDir 'mobile-assemble.log'

@(
    "CI-like run timestamp: $timestamp",
    "Workspace: $root",
    "JavaHome: $javaHome"
) | Set-Content -Path $runInfoPath

$results = [ordered]@{}

Write-Section 'Backend Tests'
Push-Location (Join-Path $root 'backend')
try {
    & mvn -f pom.xml test *>&1 | Tee-Object -FilePath $backendLog
    $results.backend = @{ exitCode = $LASTEXITCODE; command = 'mvn -f backend/pom.xml test'; log = $backendLog }
    if ($LASTEXITCODE -ne 0) {
        throw "Backend tests failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

Write-Section 'Web Build'
Push-Location (Join-Path $root 'web')
try {
    & npm run build *>&1 | Tee-Object -FilePath $webLog
    $results.web = @{ exitCode = $LASTEXITCODE; command = 'npm run build (in web/)'; log = $webLog }
    if ($LASTEXITCODE -ne 0) {
        throw "Web build failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

Write-Section 'Mobile Assemble'
$previousJavaHome = $env:JAVA_HOME
$previousPath = $env:Path
try {
    $env:JAVA_HOME = $javaHome
    $env:Path = "$javaBin;$env:Path"

    Push-Location (Join-Path $root 'mobile')
    try {
        & .\gradlew.bat assembleDebug *>&1 | Tee-Object -FilePath $mobileLog
        $results.mobile = @{ exitCode = $LASTEXITCODE; command = 'gradlew assembleDebug (in mobile/)'; log = $mobileLog; javaHome = $javaHome }
        if ($LASTEXITCODE -ne 0) {
            throw "Mobile assemble failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
} finally {
    $env:JAVA_HOME = $previousJavaHome
    $env:Path = $previousPath
}

$summary = [ordered]@{
    runId = $timestamp
    timestamp = (Get-Date).ToString('o')
    artifactDir = $artifactDir
    backend = $results.backend
    web = $results.web
    mobile = $results.mobile
    overall = 'PASS'
}

$summary | ConvertTo-Json -Depth 6 | Set-Content -Path $summaryJsonPath
@"
# CI-like Run Summary

- Run ID: $timestamp
- Timestamp: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
- Overall: PASS

## Steps
- Backend tests: PASS
- Web build: PASS
- Mobile assemble: PASS
- Mobile JAVA_HOME: $javaHome

## Artifacts
- $runInfoPath
- $backendLog
- $webLog
- $mobileLog
- $summaryJsonPath
"@ | Set-Content -Path $summaryMdPath

Write-Host "`nArtifacts written to: $artifactDir" -ForegroundColor Green
Write-Host "Summary: $summaryMdPath" -ForegroundColor Green
