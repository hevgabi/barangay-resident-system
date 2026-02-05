@echo off
cd /d "%~dp0"
start "" javaw -cp "BarangayResidentSystem.jar;lib/*" app.Main
exit
