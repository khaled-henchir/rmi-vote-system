@echo off
echo Starting serveur...
call "%~dp0serveur\run.bat"

echo Starting client...
call "%~dp0client\run.bat"

echo All batch files executed.

pause
