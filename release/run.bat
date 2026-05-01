@echo off
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java n'est pas installe sur votre machine.
    echo Ouverture de la page de telechargement Java 21...
    start https://adoptium.net/temurin/releases/?version=21
    pause
    exit
)
java -jar "%~dp0sokoban-1.0-SNAPSHOT.jar"
pause
