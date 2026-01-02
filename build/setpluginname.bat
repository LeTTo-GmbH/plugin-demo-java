@echo off

set startdir=%cd%

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

rem Datei prüfen und Ersetzungen durchführen
set "FileTemplate=%dir%\build\templates\docker-service-plugin.yml"
set "File=%dir%\yml\docker-service-plugin.yml"
copy %FileTemplate% %File%
rem Benutze PowerShell für sichere, literale Ersetzungen (case-sensitive).
powershell -NoProfile -Command ^
  "$ep = '%endpoint%'; $epUp = $ep.ToUpper(); $path = '%File%'; if(-not (Test-Path $path)){ Write-Host 'Datei nicht gefunden'; exit 2 }; $c = Get-Content -Raw -Encoding UTF8 $path; $c = $c.Replace('plugindemo',$ep).Replace('PLUGINDEMO',$epUp); Set-Content -Encoding UTF8 -Path $path -Value $c"
echo %File% wurde erzeugt.

set "FileTemplate=%dir%\build\templates\application.properties"
set "File=%dir%\src\main\resources\application.properties"
copy %FileTemplate% %File%
rem Benutze PowerShell für sichere, literale Ersetzungen (case-sensitive).
powershell -NoProfile -Command ^
  "$ep = '%endpoint%'; $epUp = $ep.ToUpper(); $path = '%File%'; if(-not (Test-Path $path)){ Write-Host 'Datei nicht gefunden'; exit 2 }; $c = Get-Content -Raw -Encoding UTF8 $path; $c = $c.Replace('plugindemo',$ep).Replace('PLUGINDEMO',$epUp); Set-Content -Encoding UTF8 -Path $path -Value $c"
echo %File% wurde erzeugt.

git rm -r "src\main\resources\static"
rd    src\main\resources\static /s /q
mkdir src\main\resources\static
mkdir src\main\resources\static\%endpoint%
mkdir src\main\resources\static\%endpoint%\open
xcopy build\templates\open\* src\main\resources\static\%endpoint%\open\ /e /s /q /y

set "FileTemplate=%dir%\build\templates\head_include.html"
set "File=%dir%\src\main\resources\static\%endpoint%\open\head_include.html"
copy %FileTemplate% %File%
rem Benutze PowerShell für sichere, literale Ersetzungen (case-sensitive).
powershell -NoProfile -Command ^
  "$ep = '%endpoint%'; $epUp = $ep.ToUpper(); $path = '%File%'; if(-not (Test-Path $path)){ Write-Host 'Datei nicht gefunden'; exit 2 }; $c = Get-Content -Raw -Encoding UTF8 $path; $c = $c.Replace('plugindemo',$ep).Replace('PLUGINDEMO',$epUp); Set-Content -Encoding UTF8 -Path $path -Value $c"
echo %File% wurde erzeugt.

set "varsFile=%dir%\build\vars.bat"

echo @echo off >%varsFile%
echo rem öffentlicher Pfad des Plugins direkt nach dem DNS-Namen in der url - https://dns.name.at/urlpath/ >>%varsFile%
echo set urlpath=%endpoint% >>%varsFile%

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
