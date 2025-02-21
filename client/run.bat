@echo off
cd /d "%~dp0"
javac Client.java
start /B java Client
