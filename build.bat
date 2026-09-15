@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not exist bin mkdir bin
if not exist dist mkdir dist
if exist dist\jpkg rmdir /s /q dist\jpkg
mkdir dist\jpkg

echo Compiling sources...
dir /s /b src\*.java > "%TEMP%\pokemon-sources.txt"
javac --release 21 -d bin @"%TEMP%\pokemon-sources.txt"
if errorlevel 1 (
  echo Modular compile failed, retrying without module-info...
  dir /s /b src\main\*.java src\entidade\*.java src\tile\*.java > "%TEMP%\pokemon-sources.txt"
  javac --release 21 -d bin @"%TEMP%\pokemon-sources.txt"
)
if errorlevel 1 (
  echo Compile failed.
  del "%TEMP%\pokemon-sources.txt" >nul 2>&1
  exit /b 1
)
del "%TEMP%\pokemon-sources.txt" >nul 2>&1

echo Copying resources into bin...
xcopy /E /I /Y res\* bin\ >nul

echo Staging classes and resources...
if exist dist\classes rmdir /s /q dist\classes
mkdir dist\classes
xcopy /E /I /Y bin\* dist\classes\ >nul
xcopy /E /I /Y res\* dist\classes\ >nul
if exist dist\classes\module-info.class del dist\classes\module-info.class

echo Creating JAR...
jar --create --file dist\jpkg\pokemon.jar --main-class main.Main -C dist\classes .
if errorlevel 1 (
  echo JAR creation failed.
  exit /b 1
)
copy /Y dist\jpkg\pokemon.jar dist\pokemon.jar >nul

where jpackage >nul 2>&1
if errorlevel 1 (
  echo jpackage was not found on PATH.
  echo Runnable JAR created at dist\pokemon.jar
  echo Run it with: java -jar dist\pokemon.jar
  exit /b 0
)

echo Packaging Windows app-image...
if exist dist\PokemonGame rmdir /s /q dist\PokemonGame
jpackage --input dist\jpkg --name PokemonGame --main-jar pokemon.jar --main-class main.Main --type app-image --dest dist --app-version 1.0
if errorlevel 1 (
  echo jpackage failed. JAR is still available at dist\pokemon.jar
  exit /b 1
)

echo Done.
echo EXE: dist\PokemonGame\PokemonGame.exe
echo JAR: dist\pokemon.jar
endlocal
