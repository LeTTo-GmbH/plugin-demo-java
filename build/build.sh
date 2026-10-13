#!/bin/bash
# Linux-Gegenstück zu build.bat:
# baut JavaDoc, Spring-Boot-Jar und Docker-Image und startet den Container im LeTTo-Compose-Verzeichnis neu

startdir=$(pwd)
composedir=${LETTO_COMPOSE_DIR:-/opt/letto/docker/compose/letto}

fail() {
  echo "BUILD fehlerhaft - Abbruch! $1"
  cd "$startdir"
  exit 1
}

echo wechsel ins Hauptverzeichnis
dir=$(cd "$(dirname "$0")" && pwd)
while [ ! -e "$dir/plugin-demo-java.info" ]; do
  [ "$dir" == "/" ] && fail "Hauptverzeichnis kann nicht gefunden werden !!"
  dir=$(dirname "$dir")
done
cd "$dir" || fail

# öffentlicher Pfad des Plugins direkt nach dem DNS-Namen in der url - https://dns.name.at/urlpath/
# wird wie bei build.bat aus build/vars.bat gelesen (dort von setpluginname.bat eingetragen)
urlpath=$(sed -n 's/^set "urlpath=\([^"]*\)".*/\1/p' build/vars.bat | tr -d '\r')
[ -z "$urlpath" ] && urlpath=plugindemo
echo "Pluginpath=$urlpath"
cp revision.txt src/main/resources/revision.txt

# Maven aus dem PATH, sonst der Maven-Wrapper des Projekts
if command -v mvn >/dev/null; then mvn=mvn; else mvn=./mvnw; fi
$mvn -version

# Name des Docker-Images
image="lettohub/letto-service-$urlpath:stable"
# Bereinige den Build
$mvn clean -DinteractiveMode=false -DskipTests || fail "mvn clean"
# erzeuge die JavaDOC
$mvn javadoc:javadoc || fail "mvn javadoc"
# kopiere die JavaDOC ins static-Verzeichnis am Webserver
rm -rf "src/main/resources/static/$urlpath/open/javadoc"
mkdir -p "src/main/resources/static/$urlpath/open/javadoc"
cp -r target/reports/apidocs/. "src/main/resources/static/$urlpath/open/javadoc/"
echo bilde den Spring-Boot-Server
$mvn package -DinteractiveMode=false -DskipTests || fail "mvn package"
echo plugins packaged

echo "beende eine laufende Version des Docker-Containers docker-service-$urlpath.yml"
if [ -e "$composedir/docker-service-$urlpath.yml" ]; then
  (cd "$composedir" && docker compose -f "docker-service-$urlpath.yml" down)
fi

echo "bilde den Docker-Container neu $image"
docker rmi "$image" --force
docker build -f Dockerfile -t "$image" . || fail "docker build"
echo "docker $urlpath built"

echo "kopiere die yml-Datei ins compose-Verzeichnis und starte den Docker-Container neu docker-service-$urlpath.yml"
cp yml/docker-service-plugin.yml "$composedir/docker-service-$urlpath.yml" || fail "yml kopieren"
(cd "$composedir" && docker compose -f "docker-service-$urlpath.yml" up -d) || fail "docker compose up"
echo "$urlpath built and started"

echo BUILD fertig!
cd "$startdir"
