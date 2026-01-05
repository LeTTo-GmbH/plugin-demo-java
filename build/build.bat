@echo off

rem verwendete Java-Version
set JAVA_HOME=C:\Program Files\Java\jdk-21

call mvn -version
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
rem öffentlicher Pfad des Plugins direkt nach dem DNS-Namen in der url - https://dns.name.at/urlpath/ - wird durch vars.bat überschrieben
set "urlpath=plugindemo"
echo Pluginpath=%urlpath%!
call build\vars.bat
copy revision.txt src\main\resources\revision.txt

rem Name des Docker-Images
set "image=lettohub/letto-service-%urlpath%:stable"
rem Bereinige den Build
call mvn clean -DinteractiveMode=false -DskipTests
rem erzeuge die JavaDOC
call mvn javadoc:javadoc
rem kopiere die JavaDOC ins static-Verzeichnis am Webserver
rmdir src\main\resources\static\%urlpath% /s /q
rmdir src\main\resources\static\%urlpath%\open\javadoc /s /q
xcopy target\reports\apidocs src\main\resources\static\%urlpath%\open\javadoc /E /S /Y /I
echo bilde den Spring-Boot-Server
call mvn package -DinteractiveMode=false -DskipTests
IF %ERRORLEVEL% EQU 0 (echo plugins packaged)  ELSE goto BUILDFAIL

echo beende eine laufende Version des Docker-Containers docker-service-%urlpath%.yml
cd \opt\letto\docker\compose\letto
docker compose -f docker-service-%urlpath%.yml down
rem IF %ERRORLEVEL% EQU 0 (echo docker setup stopped)  ELSE goto BUILDFAIL

echo bilde den Docker-Container neu %image%
cd %dir%
docker rmi %image% --force
docker build -f Dockerfile -t %image% .
IF %ERRORLEVEL% EQU 0 (echo docker %urlpath% built)  ELSE goto BUILDFAIL

echo kopiere die yml-Datei ins compose-Verzeichnis und starte den Docker-Container neu docker-service-%urlpath%.yml
copy %dir%\yml\docker-service-plugin.yml \opt\letto\docker\compose\letto\docker-service-%urlpath%.yml
cd \opt\letto\docker\compose\letto
docker compose -f docker-service-%urlpath%.yml up -d
IF %ERRORLEVEL% EQU 0 (echo %urlpath% built and started)  ELSE goto BUILDFAIL

rem docker push %image%
rem ssh root@s3.letto.at -t ssh root@letto -t /opt/letto/docker/scripts/updatelti.sh

echo sleep
powershell -Executionpolicy Bypass -Command "sleep 3"

echo BUILD fertig!
goto FIN
rem ------------------------------
:BUILDFAIL
echo BUILD fehlerhaft - Abbruch!
exit 1
goto FIN
rem ------------------------------
:FIN
cd %startdir%
