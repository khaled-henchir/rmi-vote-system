@echo off
echo Starting first batch file...
call "%~dp0serveur\run.bat"

echo Starting second batch file...
call "%~dp0client\run.bat"

echo All batch files executed.

pause