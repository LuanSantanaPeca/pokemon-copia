@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not exist lib\junit-platform-console-standalone-1.11.4.jar (
  echo Missing lib\junit-platform-console-standalone-1.11.4.jar
  exit /b 1
)

if not exist bin mkdir bin
if exist bin-test rmdir /s /q bin-test
mkdir bin-test

echo Compiling sources...
dir /s /b src\main\*.java src\entidade\*.java src\tile\*.java > "%TEMP%\pokemon-test-src.txt"
javac --release 21 -encoding UTF-8 -d bin @"%TEMP%\pokemon-test-src.txt"
if errorlevel 1 (
  echo Source compile failed.
  del "%TEMP%\pokemon-test-src.txt" >nul 2>&1
  exit /b 1
)
del "%TEMP%\pokemon-test-src.txt" >nul 2>&1
xcopy /E /I /Y res\* bin\ >nul

echo Compiling tests...
dir /s /b test\*.java > "%TEMP%\pokemon-test-files.txt"
javac --release 21 -encoding UTF-8 -d bin-test -cp "bin;lib\junit-platform-console-standalone-1.11.4.jar" @"%TEMP%\pokemon-test-files.txt"
if errorlevel 1 (
  echo Test compile failed.
  del "%TEMP%\pokemon-test-files.txt" >nul 2>&1
  exit /b 1
)
del "%TEMP%\pokemon-test-files.txt" >nul 2>&1

echo Running tests...
java -jar lib\junit-platform-console-standalone-1.11.4.jar execute --class-path "bin-test;bin;res;lib\junit-platform-console-standalone-1.11.4.jar" --scan-class-path
endlocal
