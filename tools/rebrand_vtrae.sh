// FILE_PATH: tools/rebrand_vtrae.ps1
// ACTION: CREATE
// DESCRIPTION: Windows PowerShell script to automate the package rename from imagis to vtrae
// ---------------------------------------------------------
$ErrorActionPreference = "Stop"

Write-Host "Initiating VTR Æ Rebrand Sequence for Windows..." -ForegroundColor Cyan

$oldPackage = "com.epicgera.imagis"
$newPackage = "com.epicgera.vtrae"
$oldDir = "app\src\main\java\com\epicgera\imagis"
$newDir = "app\src\main\java\com\epicgera\vtrae"

# 1. Create the new directory structure
Write-Host "Creating new directory structure..."
if (-not (Test-Path -Path $newDir)) {
    New-Item -ItemType Directory -Force -Path $newDir | Out-Null
}

# 2. Move all files to the new directory
Write-Host "Moving source files..."
if (Test-Path -Path $oldDir) {
    Move-Item -Path "$oldDir\*" -Destination $newDir -Force
    Remove-Item -Path "app\src\main\java\com\epicgera\imagis" -Recurse -Force
} else {
    Write-Host "Old directory not found. It may have already been moved." -ForegroundColor Yellow
}

# 3. Find and Replace in all Kotlin, XML, Gradle, and ProGuard files
Write-Host "Updating imports and package declarations across the project..."
$filesToUpdate = Get-ChildItem -Path . -Include *.kt, *.xml, *.kts, *.pro -Recurse | Where-Object { -not $_.FullName.Contains("\build\") }

foreach ($file in $filesToUpdate) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match [regex]::Escape($oldPackage)) {
        $content -replace [regex]::Escape($oldPackage), $newPackage | Set-Content $file.FullName
        Write-Host "Updated: $($file.Name)" -ForegroundColor DarkGray
    }
}

# 4. Update the App Name in strings.xml specifically
Write-Host "Injecting VTR Æ into strings.xml..."
$stringsFile = "app\src\main\res\values\strings.xml"
if (Test-Path $stringsFile) {
    $xmlContent = Get-Content $stringsFile -Raw
    $xmlContent = $xmlContent -replace ">iMagis<", ">VTR Æ<"
    Set-Content -Path $stringsFile -Value $xmlContent
}

Write-Host "Rebrand complete. Please run '.\gradlew clean assembleDebug' to verify integrity." -ForegroundColor Green