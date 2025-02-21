@echo off
cd /d "%~dp0"
javac *.java
start /B java -cp ".;mysql-connector-j-9.1.0.jar" Serveur
