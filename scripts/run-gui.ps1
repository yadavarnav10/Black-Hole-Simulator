$projectRoot = Split-Path -Parent $PSScriptRoot
$javaFxLib = Join-Path $projectRoot "lib\javafx-sdk-21.0.12\lib"
$buildDirectory = Join-Path $projectRoot ".build"

if (-not (Test-Path (Join-Path $javaFxLib "javafx.controls.jar"))) {
    throw "JavaFX was not found. Run .\scripts\setup-javafx.ps1 first."
}

New-Item -ItemType Directory -Force -Path $buildDirectory | Out-Null
& javac --module-path $javaFxLib --add-modules javafx.controls -d $buildDirectory `
    "$projectRoot\Main.java" "$projectRoot\Black_Hole.java" "$projectRoot\particle.java" `
    "$projectRoot\State.java" "$projectRoot\RelativisticSim.java" "$projectRoot\RK4_Integrator.java"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Copy-Item -LiteralPath "$projectRoot\style.css" -Destination $buildDirectory -Force
& java --module-path $javaFxLib --add-modules javafx.controls -cp $buildDirectory Main
