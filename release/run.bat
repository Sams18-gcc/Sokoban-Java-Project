@echo off
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java n'est pas installe.
    echo Ouverture de la page de telechargement...
    start https://adoptium.net/temurin/releases/?version=21
    pause
    exit
)
java -jar "%~dp0sokoban-1.0-SNAPSHOT.jar"
