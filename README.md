# Plugin Demoprojekt in Java

## github:
https://github.com/LeTTo-GmbH/plugin-demo-java.git

# Erstellen einen neuen Plugins
* checkout des Demoprojektes von https://github.com/LeTTo-GmbH/plugin-demo-java.git auf einen Entwicklungsrechner
* Definiere einen Endpoint-Pfad welcher für das Plugin am Zielserver verwendet werden soll - zB.: plugindemo
  * starte aus dem Verzeichnie build das Script setpluginname.bat mit dem Endpoint-Pfad als Parameter
    >build\setpluginname.bat plugindemo
    * Das Script kopiert yml/docker-service-plugin.template.yml nach yml/docker-service-plugin.yml und ersetzt alle Vorkommen von plugindemo durch den Endpoint-Pfad
    * Weiters wird der Endpointpfad in der Datei build/vars.bat eingetragen
* 

