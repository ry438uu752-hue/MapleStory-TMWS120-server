@echo off
title TMS120 (Java 21 POC)
cd /d "%~dp0"

rem ---------------------------------------------------------------------------
rem Java 21 proof-of-concept launcher.
rem The stable launcher is Launcher.bat, which uses JDK 8.
rem
rem Requirements:
rem   - Java 21 runtime
rem   - lib\nashorn-core-15.4.jar + ASM 7.3.1 jars (bundled in dist\lib)
rem
rem Set JAVA21_HOME to your JDK/JRE 21 folder, or put java 21 on PATH.
rem ---------------------------------------------------------------------------

set "JAVA_EXE="
if defined JAVA21_HOME if exist "%JAVA21_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA21_HOME%\bin\java.exe"
if not defined JAVA_EXE (
    for /f "delims=" %%i in ('where java 2^>nul') do if not defined JAVA_EXE set "JAVA_EXE=%%i"
)

if not defined JAVA_EXE (
    echo [ERROR] Java 21 not found.
    echo         Set JAVA21_HOME to your JDK 21 folder, or put java 21 on PATH.
    pause
    exit /b 1
)

if not exist "Logs" mkdir "Logs"

echo Java used: "%JAVA_EXE%"
"%JAVA_EXE%" -Xmx512M -Dnet.sf.odinms.wzpath=wz -Dfile.encoding=UTF8 ^
  -cp ".;dist\TMS120.jar;dist\lib\*" server.Start

pause
