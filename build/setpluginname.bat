@echo off

set startdir=%cd%
echo wechsel ins Hauptverzeichnis
:LOOP1
if exist plugin-demo-java.info goto DIROK
set olddir=%cd%
cd ..
set dir=%cd%
if %olddir% == %dir% (
  echo Hauptverzeichnis kann nicht gefunden werden !!
  goto FAIL
)
goto LOOP1
:DIROK
rem Workspace automatisch ermitteln
set dir=%cd%

cd %dir%

rem ------------------------------
rem Parameter-Prüfung
rem ------------------------------
if "%1"=="" (
  echo.
  echo Verwendung: %~n0 ^<endpoint^>
  echo Beispiel: %~n0 pluginuhr
  echo.
  goto FIN
)

if NOT "%2"=="" (
  echo.
  echo Fehler: Zu viele Parameter angegeben.
  echo Verwendung: %~n0 ^<endpoint^>
  echo Beispiel: %~n0 pluginuhr
  echo.
  goto FIN
)

rem genau ein Parameter -> endpoint setzen
set "endpoint=%1"
echo Endpoint gesetzt auf: %endpoint%

rem ------------------------------
rem Datei prüfen und Ersetzungen durchführen
rem ------------------------------
set "composeFile=%dir%\yml\docker-service-plugin.yml"
set "composeFileTemplate=%dir%\yml\docker-service-plugin.template.yml"
if NOT exist "%composeFileTemplate%" (
  echo Fehler: %composeFileTemplate% nicht gefunden!
  goto BUILDFAIL
)

copy %composeFileTemplate% %composeFile%

rem Benutze PowerShell für sichere, literale Ersetzungen (case-sensitive).
powershell -NoProfile -Command ^
  "$ep = '%endpoint%'; $epUp = $ep.ToUpper(); $path = '%composeFile%'; if(-not (Test-Path $path)){ Write-Host 'Datei nicht gefunden'; exit 2 }; $c = Get-Content -Raw -Encoding UTF8 $path; $c = $c.Replace('plugindemo',$ep).Replace('PLUGINDEMO',$epUp); Set-Content -Encoding UTF8 -Path $path -Value $c"

if errorlevel 1 (
  echo Fehler bei der Bearbeitung der Datei.
  goto BUILDFAIL
)

echo %composeFile% wurde erfolgreich erzeugt.

set "varsFile=%dir%\build\vars.bat"
echo "@echo off" >%varsFile%
echo "" >>%varsFile%
echo "rem öffentlicher Pfad des Plugins direkt nach dem DNS-Namen in der url - https://dns.name.at/urlpath/" >>%varsFile%
echo "set urlpath=%endpoint%" >>%varsFile%

echo vars.bat erzeugt!

echo Plugin-Endpoint wurde auf %endpoint% gesetzt!
goto FIN
rem ------------------------------
:BUILDFAIL
echo FEHLER - Abbruch!
exit 1
goto FIN
rem ------------------------------
:FIN
cd %startdir%
