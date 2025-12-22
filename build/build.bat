@echo off

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

cd %dir%\letto-pluginuhr
call mvn clean -DinteractiveMode=false -DskipTests
call mvn javadoc:javadoc
rmdir src\main\resources\static\pluginhuhr /s /q
rmdir src\main\resources\static\pluginuhr\open\javadoc /s /q
xcopy target\site\apidocs src\main\resources\static\pluginuhr\open\javadoc /E /S /Y /I
call mvn package -DinteractiveMode=false -DskipTests
IF %ERRORLEVEL% EQU 0 (echo plugins packaged)  ELSE goto BUILDFAIL

set image=lettohub/letto-service-pluginuhr:debug

cd \opt\letto\docker\compose\letto
docker compose -f docker-service-pluginuhr.yml down
rem IF %ERRORLEVEL% EQU 0 (echo docker setup stopped)  ELSE goto BUILDFAIL

cd %dir%\letto-pluginuhr
docker rmi %image% --force
docker build -f Dockerfile -t %image% .
IF %ERRORLEVEL% EQU 0 (echo docker pluginuhr built)  ELSE goto BUILDFAIL

rem copy %dir%\letto-pluginuhr\yml\docker-service-pluginuhr.yml \opt\letto\docker\compose\letto\
cd \opt\letto\docker\compose\letto
set yml=\opt\letto\docker\compose\letto\docker-service-pluginuhr.yml
set template_yml=%dir%\letto-setup\setupservice\src\main\resources\yml\docker-service-pluginuhr.yml
set replace_bat=%dir%\letto-setup\setupservice\scripts\replacetag.bat
call %replace_bat% %yml% %template_yml%
docker compose -f docker-service-pluginuhr.yml up -d
IF %ERRORLEVEL% EQU 0 (echo pluginuhr built and started)  ELSE goto BUILDFAIL

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
