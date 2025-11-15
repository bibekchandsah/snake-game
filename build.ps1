# Snake Game - Build Script for Windows EXE
# This script compiles Java files, creates JAR, and builds EXE

Write-Host "🐍 Building Snake Game EXE..." -ForegroundColor Green

# Step 1: Compile Java files
Write-Host "`n📦 Step 1: Compiling Java files..." -ForegroundColor Cyan
javac SnakeGame.java GamePanel.java SettingsPanel.java HighScoreManager.java Food.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Compilation successful!" -ForegroundColor Green

# Step 2: Create manifest file
Write-Host "`n📄 Step 2: Creating manifest file..." -ForegroundColor Cyan
"Main-Class: SnakeGame" | Out-File -FilePath manifest.txt -Encoding ASCII
Write-Host "✅ Manifest created!" -ForegroundColor Green

# Step 3: Create JAR file
Write-Host "`n📦 Step 3: Creating JAR file..." -ForegroundColor Cyan
try {
    jar cvfm SnakeGame.jar manifest.txt *.class icon.ico icon.png
} catch {
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User"); jar cvfm SnakeGame.jar manifest.txt *.class icon.ico icon.png
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ JAR creation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ JAR created successfully!" -ForegroundColor Green

# Step 4: Check if Launch4j is available
Write-Host "`n🔨 Step 4: Building EXE with Launch4j..." -ForegroundColor Cyan

# Try to find Launch4j installation
$launch4jPaths = @(
    "C:\Program Files (x86)\Launch4j\launch4jc.exe",
    "C:\Program Files\Launch4j\launch4jc.exe",
    "$env:ProgramFiles(x86)\Launch4j\launch4jc.exe",
    "$env:ProgramFiles\Launch4j\launch4jc.exe"
)

$launch4jExe = $null
foreach ($path in $launch4jPaths) {
    if (Test-Path $path) {
        $launch4jExe = $path
        break
    }
}

if ($launch4jExe) {
    Write-Host "Found Launch4j at: $launch4jExe" -ForegroundColor Yellow
    & $launch4jExe launch4j-config.xml
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ EXE created successfully!" -ForegroundColor Green
        Write-Host "`n🎮 SnakeGame.exe is ready!" -ForegroundColor Magenta
        Write-Host "📁 Location: $(Get-Location)\SnakeGame.exe" -ForegroundColor Yellow
    } else {
        Write-Host "❌ EXE creation failed!" -ForegroundColor Red
    }
} else {
    Write-Host "⚠️  Launch4j not found!" -ForegroundColor Yellow
    Write-Host "`nPlease install Launch4j from: https://launch4j.sourceforge.net/" -ForegroundColor Cyan
    Write-Host "Or install via Chocolatey: choco install launch4j" -ForegroundColor Cyan
    Write-Host "`nJAR file created successfully. You can run it with: java -jar SnakeGame.jar" -ForegroundColor Green
}

Write-Host "`n✨ Build process complete!" -ForegroundColor Green
