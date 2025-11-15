# Convert ICO to PNG for Java icon support
# This script converts icon.ico to icon.png

Write-Host "🎨 Converting icon.ico to icon.png..." -ForegroundColor Cyan

# Check if icon.ico exists
if (Test-Path "icon.ico") {
    # Try using .NET to convert
    Add-Type -AssemblyName System.Drawing
    
    try {
        $ico = [System.Drawing.Icon]::new("icon.ico")
        $bitmap = $ico.ToBitmap()
        $bitmap.Save("icon.png", [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Host "✅ Successfully created icon.png" -ForegroundColor Green
        
        $ico.Dispose()
        $bitmap.Dispose()
    }
    catch {
        Write-Host "⚠️  Could not convert automatically: $_" -ForegroundColor Yellow
        Write-Host "`nPlease convert icon.ico to icon.png manually:" -ForegroundColor Cyan
        Write-Host "  1. Open icon.ico in Paint or image editor" -ForegroundColor White
        Write-Host "  2. Save as PNG format" -ForegroundColor White
        Write-Host "  3. Name it 'icon.png'" -ForegroundColor White
    }
}
else {
    Write-Host "❌ icon.ico not found!" -ForegroundColor Red
}
