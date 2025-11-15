# 🔨 Building Snake Game for Distribution

This guide explains how to compile the Snake Game into an executable (.exe) file with a custom icon.

## Preview

The built application includes:
- Custom window icon (icon.ico)
- Professional executable
- Embedded JAR with all resources

![Application Icon](icon.ico)

## 📦 Pre-built Downloads

**Don't want to build?** Download pre-built versions:
- **[SnakeGame.exe](https://github.com/bibekchandsah/snake-game/releases/latest/download/SnakeGame.exe)** - Windows Executable
- **[SnakeGame.jar](https://github.com/bibekchandsah/snake-game/releases/latest/download/SnakeGame.jar)** - Cross-platform JAR

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher
- Launch4j (for creating Windows EXE)
- icon.ico file (for custom icon)

## 🚀 Quick Build

### Option 1: Automated Build Script (Easiest)

Run the PowerShell build script:

```powershell
.\build.ps1
```

This script will:
1. ✅ Compile all Java files
2. ✅ Create manifest file
3. ✅ Build JAR file
4. ✅ Create EXE with Launch4j (if installed)

### Option 2: Manual Build

#### Step 1: Compile Java Files
```powershell
javac SnakeGame.java GamePanel.java SettingsPanel.java HighScoreManager.java Food.java
```

#### Step 2: Create Manifest File
```powershell
echo "Main-Class: SnakeGame" > manifest.txt
```

#### Step 3: Create JAR File
```powershell
jar cvfm SnakeGame.jar manifest.txt *.class icon.ico icon.png
```
or
```powershell
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User"); jar cvfm SnakeGame.jar manifest.txt *.class icon.ico icon.png

```

#### Step 4: Test JAR File
```powershell
java -jar SnakeGame.jar
```

#### Step 5: Create EXE with Launch4j

1. Install Launch4j from: https://launch4j.sourceforge.net/
2. Open Launch4j
3. Load the `launch4j-config.xml` configuration file
4. Click "Build wrapper" button
5. Your `SnakeGame.exe` will be created!

**OR** use command line:
```powershell
& "C:\Program Files (x86)\Launch4j\launch4jc.exe" "launch4j-config.xml"
```

## 🎨 Custom Icon

The build uses `icon.ico` as the application icon. Make sure you have this file in the same directory.

### Icon Requirements:
- Format: .ico (Windows Icon)
- Recommended size: 256x256 pixels
- Multiple resolutions (16x16, 32x32, 48x48, 256x256)

### Creating an ICO file:

**From PNG/JPG:**
- Use online converter: https://convertio.co/png-ico/
- Or use tools like GIMP, Photoshop, or IcoFX

**From Command Line (ImageMagick):**
```powershell
magick convert icon.png -define icon:auto-resize=256,128,64,48,32,16 icon.ico
```

## 📦 Distribution Package

After building, your distribution should include:

```
SnakeGame-v1.0/
├── SnakeGame.exe          # Main executable
├── SnakeGame.jar          # JAR file (bundled in EXE)
├── highscore.dat          # Created on first run (optional)
└── README.md              # User documentation
```

### For Users WITHOUT Java:

Use **jpackage** (JDK 14+) to bundle JRE:

```powershell
jpackage --input . --name SnakeGame --main-jar SnakeGame.jar --main-class SnakeGame --type exe --icon icon.ico --win-shortcut --win-menu
```

This creates a full installer with bundled JRE!

## 🔧 Alternative Methods

### Method 2: jpackage (JDK 14+)

Modern Java packaging tool with JRE bundling:

```powershell
# Create runtime image
jlink --add-modules java.desktop,java.base --output jre

# Package application
jpackage --input . `
         --name "Snake Game" `
         --main-jar SnakeGame.jar `
         --main-class SnakeGame `
         --type exe `
         --icon icon.ico `
         --runtime-image jre `
         --win-shortcut `
         --win-menu `
         --app-version 1.0
```

### Method 3: GraalVM Native Image

Compile to native executable (advanced):

```powershell
# Install GraalVM
# Then run:
native-image -jar SnakeGame.jar SnakeGame
```

## 📋 Build Checklist

- [ ] All Java files compile without errors
- [ ] JAR file runs correctly (`java -jar SnakeGame.jar`)
- [ ] icon.ico file exists and is valid
- [ ] Launch4j installed
- [ ] EXE file created successfully
- [ ] EXE runs without requiring separate Java installation (if JRE bundled)
- [ ] Icon appears in taskbar and window
- [ ] High score persistence works
- [ ] All features functional

## 🐛 Troubleshooting

### "Java not found" error when running EXE

**Solution 1:** User needs to install Java JRE
- Download from: https://www.java.com/download/

**Solution 2:** Bundle JRE with application using jpackage

### Icon not showing

- Ensure icon.ico is in correct format
- Rebuild EXE after updating icon
- Check Windows icon cache (may need restart)

### "Class not found" error

- Check manifest.txt has correct Main-Class entry
- Ensure all .class files are included in JAR
- Verify JAR file structure: `jar tf SnakeGame.jar`

### "jar not found" error
- Make sure java **bin** folder is in environment variable


## 📊 File Sizes

| File | Approximate Size |
|------|-----------------|
| SnakeGame.jar | ~50 KB |
| SnakeGame.exe (Launch4j) | ~100 KB |
| With bundled JRE | ~40-50 MB |

## 🚀 Publishing

### GitHub Releases

1. Create release on GitHub
2. Upload `SnakeGame.exe`
3. Include README.md
4. Tag version (e.g., v1.0.0)

### Installer Creation

Use **Inno Setup** or **NSIS** to create professional installer:

```powershell
# Example Inno Setup script
iscc setup-script.iss
```

## 📝 Notes

- EXE file still requires Java JRE to run (unless bundled)
- For commercial distribution, consider code signing
- Test EXE on clean Windows system
- Icon will appear in Windows taskbar and window title

## 🎉 Success!

Your distribution package should now include:

```
Release/
├── SnakeGame.exe          # Windows executable (82 KB)
├── SnakeGame.jar          # JAR file (14 KB)
├── README.md              # User instructions
├── preview1.png           # Welcome screen
├── preview2.png           # Gameplay
├── preview3.png           # Game over screen
└── LICENSE                # License file (optional)
```
