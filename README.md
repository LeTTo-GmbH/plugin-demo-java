# Plugin Demoprojekt in Java

## github:
https://github.com/LeTTo-GmbH/plugin-demo-java.git

# Erstellen einen neuen Plugins
* checkout des Demoprojektes von https://github.com/LeTTo-GmbH/plugin-demo-java.git auf einen Entwicklungsrechner
* Definiere einen Endpoint-Pfad welcher für das Plugin am Zielserver verwendet werden soll - zB.: plugindemo
  * starte aus dem Verzeichnie build das Script setpluginname.bat mit dem Endpoint-Pfad als Parameter
    >build\setpluginname.bat plugindemo
    * Die Dateien von build\templates werden in die bestehende Struktur kopiert und angepasst an den Endpoint-Pfad
      * src\main\resources\application.properties
      * src\main\resources\static (css, js, fonts, Bilder und head_include.html)
      * yml\docker-service-plugin.yml
    * Der Endpoint-Pfad wird auch im Java-Code eingetragen
      * config\Endpoint.java (servicepath)
      * config\PluginConfiguration.java (PLUGIN_NAME, Netzwerk- und Containername, interne URI)
      * controller\ApiController.java (JavaDoc-Link in der Swagger-Beschreibung)
    * Das Verzeichnis proxy wird neu erzeugt und enthält die Proxy-Konfiguration `<endpoint>.conf`
    * Weiters wird der Endpointpfad in der Datei build/vars.bat eingetragen (wird von build.bat und build.sh gelesen)
* Für jedes Plugin welches in diesem Plugin-Service laufen soll sind folgende Schritte vorzunehmen:
  * Erstelle eine Pluginklasse in at.letto.plugins.plugin welche von BasePlugin erbt
  * Registriere die Pluginklasse in der StartupConfiguration mit Name und vollständigem Klassennamen
  * Erstelle ein Verzeichnis für die Java-Script und HTML-Resourcen in src/main/resources/plugins
  * Programmiere das Plugin in der erstellten Pluginklasse und den HTML und JS-Dateien um JS-Resourcen-Verzeichnis
  * In den Hilfe-Dateien des Plugins (helpfiles) wird der Platzhalter {{servicepath}} durch den Endpoint-Pfad ersetzt, z.B. für Links zur Swagger-UI
* Entferne das Uhr-Plugin aus der Plugin-Registrierung in der Startup-Configuration

# Dateien des Uhr Plugins:

| Datei                                                    | Beschreibung           | Sprache/Format |
|----------------------------------------------------------|------------------------|----------------|
| src/main/java/at/letto/plugins/plugin/uhr/PluginUhr.java | Hauptdatei des Plugins | Java           |
| src/main/resources/plugins/uhr/uhr1Script.js             | Javascript des Plugins | JavaScript     |
| src/main/resources/plugins/uhr/uhr1ConfigScript.js       | Konfigurationsdialog   | JavaScript     |
| src/main/resources/plugins/uhr/Uhr1.html                 | Hilfetext              | HTML           |

# Hilfeseite des Plugin-Services
* Unter /&lt;endpoint&gt;/, /&lt;endpoint&gt;/index.html und /&lt;endpoint&gt;/help liefert der Service eine Hilfeseite mit Links zu Swagger-UI und JavaDoc
* Die Seite liegt in src/main/resources/help/index.html, die Platzhalter {{servicepath}}, {{name}} und {{version}} werden vom HelpController ersetzt

# Builden des Plugins
* unter Windows mit dem Script 
  >build\build.bat
* unter Linux mit dem Script 
  >build/build.sh
* Beide Scripts erzeugen JavaDoc, Jar und Docker-Image und starten den Container im Verzeichnis /opt/letto/docker/compose/letto neu

# Debuggen und Testen des Plugins
* Am Entwicklungsrechner muss eine Letto-Installation vorhanden sein
* beim Builden des Plugins mit build.bat oder build.sh wird automatisch auch der Docker-Container gestartet und sollte sofort mit dem Plugintester und im Debugger getestet werden können
* kopiere die Datei *.conf aus dem Verzeichnis proxy in das Verzeichnis /opt/letto/docker/proxy
* starten den proxy neu
  >docker restart letto-proxy


