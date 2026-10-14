# letto-service-plugindemo

Referenz-Plugin für **[LeTTo](https://www.letto.at)** in Java (Spring Boot) – mit dem Beispiel-Plugin **Uhr1**.

Das Image enthält einen Plugin-Service, der neben dem LeTTo-Server als eigener Container im Docker-Netzwerk `nw-letto`
läuft. LeTTo kommuniziert mit dem Plugin ausschließlich über eine REST-Schnittstelle (JSON). Das Demo-Plugin **Uhr1**
zeigt ein Uhrblatt, auf dem Schüler:innen eine Uhrzeit einstellen, die mit der Musterlösung verglichen wird. Es dient
zur Veranschaulichung der Plugin-Schnittstelle und als **Vorlage für eigene Plugins** – nicht für den Unterricht.

## Dokumentation

Die vollständige Dokumentation liegt unter **[doc.letto.at – Referenz-Plugin (Java)](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/)**:

* [Architektur](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/architektur/) – Aufbau des Services, Paketstruktur, Ablauf zwischen LeTTo-Server und Plugin
* [Eigenes Plugin erstellen](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/eigenes-plugin/) – Schritt-für-Schritt-Anleitung aus dem Demoprojekt
* [Plugin-API](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/plugin-api/) – `BasePlugin` und `PluginService`, am Beispiel Uhr
* [REST-Schnittstelle](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/rest-schnittstelle/) – alle Controller und Endpunkte
* [DTOs](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/dtos/) und [Enums](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/enums/) der Schnittstelle
* [Build und Deployment](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/build-deployment/) – Build-Scripts, Docker, Proxy, Registrierung, Debugging
* [Übersicht aller LeTTo-Plugins](https://doc.letto.at/wiki/Plugins/) · [Referenz-Plugin (Python)](https://doc.letto.at/wiki/Plugins/referenz-plugin-python/) mit derselben REST-Schnittstelle

Quellcode: **[github.com/LeTTo-GmbH/plugin-demo-java](https://github.com/LeTTo-GmbH/plugin-demo-java)**

## Tags

| Tag | Inhalt |
|---|---|
| `stable` | aktueller Stand von `master` |

## Technische Daten

| Eigenschaft | Wert |
|---|---|
| Basis-Image | `eclipse-temurin:25-jdk-jammy` (Ubuntu 22.04, Zeitzone Europe/Vienna) |
| Sprache / Framework | Java 25, Spring Boot 4 |
| API-Doku | springdoc-openapi (Swagger-UI), Beschreibungen aus der JavaDoc |
| Endpoint-Pfad | `/plugindemo` |
| Container / Hostname | `letto-plugindemo` |
| Ports | `8080` HTTP, `5080` Remote-Debugging (JDWP, nur mit `debug=true`) |
| Healthcheck | alle 30 s `http://localhost:8080/ping` → `pong` |
| Log | `/log/start.log` |

## Verwendung

Das Image ist für den Betrieb in einer bestehenden LeTTo-Installation gedacht. Die Compose-Datei liegt im Repository
unter [`yml/docker-service-plugin.yml`](https://github.com/LeTTo-GmbH/plugin-demo-java/blob/master/yml/docker-service-plugin.yml)
und wird ins Compose-Verzeichnis des LeTTo-Servers kopiert:

```bash
cp yml/docker-service-plugin.yml /opt/letto/docker/compose/letto/docker-service-plugindemo.yml
cd /opt/letto/docker/compose/letto
docker compose -f docker-service-plugindemo.yml up -d
```

Gekürzter Ausschnitt:

```yaml
networks:
  nw-letto:
    external: true

services:
  plugindemo:
    image: lettohub/letto-service-plugindemo:stable
    restart: always
    networks:
      - nw-letto
    hostname: letto-plugindemo
    container_name: letto-plugindemo
    volumes:
      - ${VOLUME_LOG:-/opt/letto/docker/storage/log}/plugindemo:/log
      - ${VOLUME_IMAGES:-/opt/letto/docker/storage/images}:/opt/letto/images
      - ${VOLUME_PLUGINS:-/opt/letto/docker/storage/plugins}:/opt/letto/plugins
    environment:
      - letto_setup_uri=${LETTO_SETUP_URI:-http://letto-setup.nw-letto:8096}
      - letto_user_user_password=${SERVICE_USER_PASSWORD}
      - servername=${SERVER_NAME}
      - letto.plugin.uri.intern=${LETTO_PLUGINDEMO_URI_INTERN:-http://letto-plugindemo.nw-letto:8080}
      - letto.plugin.uri.extern=${LETTO_PLUGIN_URI_EXTERN:-https://${SERVER_NAME}}
      - debug=${DEBUG_PLUGINS:-${DEBUG:-false}}
    ports:
      - "${DEBUG_PORT_PLUGINDEMO:-5206}:5080"
```

Beim Start registriert sich der Service selbst am Setup-Service des LeTTo-Servers (`letto_setup_uri`). Damit die
externen Endpunkte und die Hilfeseite erreichbar sind, muss zusätzlich die nginx-Konfiguration
[`proxy/plugindemo.conf`](https://github.com/LeTTo-GmbH/plugin-demo-java/tree/master/proxy) eingespielt werden:

```bash
cp proxy/*.conf /opt/letto/docker/proxy/
docker restart letto-proxy
```

### Wichtige Umgebungsvariablen

| Variable | Bedeutung |
|---|---|
| `letto_setup_uri` | URI des Setup-Services (Standard `http://letto-setup.nw-letto:8096`) |
| `letto_user_user_password` | Passwort für die Anmeldung am Setup-Service |
| `servername` | Servername der LeTTo-Installation |
| `letto.plugin.uri.intern` | interne URI des Plugins (Standard `http://letto-plugindemo.nw-letto:8080`) |
| `letto.plugin.uri.extern` | externe Basis-URI (Standard `https://${SERVER_NAME}`) |
| `letto_pathImages`, `letto_webPathImages` | Verzeichnis und Web-Pfad der Bilder |
| `letto_pathPlugins`, `letto_webPathPlugins` | Verzeichnis und Web-Pfad der Plugin-Bilder |
| `JAVA_OPTS` | Java-Optionen (Standard `-Xms50m -Xmx500m`) |
| `debug` | `true` startet Java mit Remote-Debugging auf Port 5080 |

Alle Variablen sind unter [Build und Deployment](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/build-deployment/) beschrieben.

## Am eigenen Server

Bei installiertem Plugin und eingespielter Proxy-Konfiguration:

* Hilfeseite: `https://<servername>/plugindemo/help`
* Swagger-UI: `https://<servername>/plugindemo/swagger-ui.html`
* OpenAPI (JSON): `https://<servername>/plugindemo/v3/api-docs`
* JavaDoc: `https://<servername>/plugindemo/open/javadoc/index.html`
* Erreichbarkeit: `https://<servername>/plugindemo/open/ping` → `pong`
* Plugintester: `https://<servername>/plugintester` (über das Setup-Service installierbar)

## Eigenes Plugin

Für ein eigenes Plugin wird das Repository ausgecheckt, mit `build/setpluginname.bat` ein eigener Endpoint-Pfad gesetzt
und daraus ein Image `lettohub/letto-service-<endpoint>:stable` gebaut. Die Anleitung steht unter
[Eigenes Plugin erstellen](https://doc.letto.at/wiki/Plugins/referenz-plugin-java/eigenes-plugin/).

---

Maintainer: [LeTTo GmbH](https://www.letto.at)
