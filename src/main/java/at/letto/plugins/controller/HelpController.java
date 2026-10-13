package at.letto.plugins.controller;

import at.letto.plugins.config.Endpoint;
import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.tools.PluginResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Liefert die Hilfeseite des Plugin-Services mit einer kurzen Beschreibung
 * und Links zur JavaDoc und zur Swagger-Dokumentation.<br>
 * Die Seite liegt in src/main/resources/help/index.html, die Platzhalter
 * {{servicepath}}, {{name}} und {{version}} werden beim Laden ersetzt.
 */
@RestController
public class HelpController {

    /**
     * Pfad der Hilfeseite im Klassenpfad.
     */
    private static final String HELP_RESOURCE = "help/index.html";

    /**
     * Hilfeseite mit eingesetzten Platzhaltern, wird einmal beim Start geladen.
     */
    private final String helpPage = PluginResource.readResourceFileString(HELP_RESOURCE)
            .replace("{{servicepath}}", Endpoint.servicepath)
            .replace("{{name}}", PluginConfiguration.PLUGIN_NAME)
            .replace("{{version}}", PluginConfiguration.PLUGIN_VERSION);

    /**
     * Liefert die Hilfeseite.
     *
     * @return Hilfeseite als HTML
     */
    @GetMapping(value = {Endpoint.servicepath, Endpoint.servicepath + "/", Endpoint.servicepath + "/index.html", Endpoint.servicepath + "/help"},
            produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> help() {
        return ResponseEntity.ok(helpPage);
    }

}
