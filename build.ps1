Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Budowanie z modulami JavaFX" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n[1/4] Kompilacja..." -ForegroundColor Yellow
mvn clean package
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "`n[2/4] Przygotowanie..." -ForegroundColor Yellow

if (Test-Path "target\app-input") { Remove-Item "target\app-input" -Recurse -Force }
if (Test-Path "target\ShapeGrammar") { Remove-Item "target\ShapeGrammar" -Recurse -Force }

New-Item -Path "target\app-input" -ItemType Directory -Force | Out-Null
New-Item -Path "target\app-input\libs" -ItemType Directory -Force | Out-Null

Copy-Item "target\ShapeGrammar-1.0-SNAPSHOT.jar" "target\app-input\ShapeGrammar.jar"
Copy-Item "target\libs\*.jar" "target\app-input\libs\" -Force

$javafxPath = "target\libs"

Write-Host "[OK] Struktura gotowa" -ForegroundColor Green
Write-Host "JavaFX libs: $javafxPath" -ForegroundColor Gray

Write-Host "`n[3/4] jpackage z module-path..." -ForegroundColor Yellow

$jpackage = "C:\Program Files\Java\jdk-25\bin\jpackage.exe"

& $jpackage `
    --type app-image `
    --name "ShapeGrammar" `
    --app-version 1.0 `
    --input target\app-input `
    --main-jar ShapeGrammar.jar `
    --main-class org.example.SuperMain `
    --dest target `
    --module-path "$javafxPath" `
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base `
    --java-options "-Djava.library.path=`$APPDIR" `
    --verbose

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nBLAD jpackage!" -ForegroundColor Red
    Read-Host "`nNacisnij Enter"
    exit 1
}

Write-Host "`n[4/4] Test uruchomienia..." -ForegroundColor Yellow

Write-Host "`nStruktura app:" -ForegroundColor Gray
Get-ChildItem "target\ShapeGrammar\app" | Select-Object Name

Write-Host "`nTest 1: Uruchomienie .exe..." -ForegroundColor Yellow
$process = Start-Process -FilePath "target\ShapeGrammar\ShapeGrammar.exe" -PassThru -WindowStyle Hidden

Start-Sleep -Seconds 3

if ($process.HasExited) {
    Write-Host "[PROBLEM] Aplikacja zakonczyla sie natychmiast (kod: $($process.ExitCode))" -ForegroundColor Red

    Write-Host "`nTest 2: Sprawdzanie Java runtime..." -ForegroundColor Yellow
    & "target\ShapeGrammar\runtime\bin\java.exe" -version

    Write-Host "`nTest 3: Reczne uruchomienie z logami..." -ForegroundColor Yellow
    & "target\ShapeGrammar\runtime\bin\java.exe" `
        -classpath "target\ShapeGrammar\app\ShapeGrammar.jar;target\ShapeGrammar\app\libs\*" `
        --module-path "target\ShapeGrammar\app\libs" `
        --add-modules javafx.controls,javafx.fxml,javafx.graphics `
        org.example.SuperMain

} else {
    Write-Host "[OK] Aplikacja dziala!" -ForegroundColor Green
}

Read-Host "`nNacisnij Enter"