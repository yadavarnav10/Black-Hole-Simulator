param(
    [string]$Version = "21.0.12"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$libraryRoot = Join-Path $projectRoot "lib"
$sdkDirectory = Join-Path $libraryRoot "javafx-sdk-$Version"
$sdkArchiveName = "openjfx-$($Version)_windows-x64_bin-sdk.zip"
$sdkZip = Join-Path $libraryRoot $sdkArchiveName
$downloadUrl = "https://download2.gluonhq.com/openjfx/$Version/$sdkArchiveName"

if (Test-Path (Join-Path $sdkDirectory "lib\javafx.controls.jar")) {
    Write-Host "JavaFX $Version is already available at $sdkDirectory"
    exit 0
}

New-Item -ItemType Directory -Force -Path $libraryRoot | Out-Null
Write-Host "Downloading JavaFX $Version for Windows x64..."
Invoke-WebRequest -Uri $downloadUrl -OutFile $sdkZip
Expand-Archive -Path $sdkZip -DestinationPath $libraryRoot -Force
Remove-Item -LiteralPath $sdkZip
Write-Host "JavaFX is ready at $sdkDirectory"
