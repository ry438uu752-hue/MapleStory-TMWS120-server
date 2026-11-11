@echo off
title TMS120
cd /d "%~dp0"

rem This server MUST run on Java 8: its scripts use Nashorn (removed in Java 15+).
set "JAVA_EXE=C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"
if not exist "%JAVA_EXE%" (
    echo [ERROR] JDK 8 not found: %JAVA_EXE%
    echo This server requires Java 8 because its scripts use Nashorn.
    pause
    exit /b 1
)

if not exist "Logs" mkdir "Logs"

"%JAVA_EXE%" -Xmx512M -server -Dnet.sf.odinms.wzpath=wz -cp ".;dist\*" server.Start
pause
