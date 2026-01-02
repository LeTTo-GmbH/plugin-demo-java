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
      * src\main\resources\static
      * yml\docker-service-plugin.yml
    * Weiters wird der Endpointpfad in der Datei build/vars.bat eingetragen
* Für jedes Plugin welches in diesem Plugin-Service laufen soll sind folgende Schritte vorzunehmen:
  * Erstelle eine Pluginklasse in at.letto.plugin.plugins welche von BasePlugin erbt
  * Registriere die Pluginklasse in der StartupConfiguration mit Name und Klasse
  * Erstelle ein Verzeichnis für die Java-Script und HTML-Resourcen in src/main/resources/plugins
  * Programmiere das Plugin in der erstellten Pluginklasse und den HTML und JS-Dateien um JS-Resourcen-Verzeichnis
* Entferne das Uhr-Plugin aus der Plugin-Registrierung in der Startup-Configuration

# Builden des Plugins
* mit dem Script 
  >build\build.bat

# Debuggen und Testen des Plugins
* Am Entwicklungsrechner muss eine Letto-Installation vorhanden sein
* beim Builden des Plugins mit build.bat wird automatisch auch der Docker-Container gestartet und sollte sofort mit dem Plugintester und im Debugger getestet werden können


