@echo off
setlocal
cd /d "%~dp0"
if not exist target\javicon-browser.jar (
    echo Building project...
    call mvn -q package
)
start "" java -jar target\javicon-browser.jar
endlocal