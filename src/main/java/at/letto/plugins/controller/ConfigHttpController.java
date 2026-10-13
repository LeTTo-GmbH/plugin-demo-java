package at.letto.plugins.controller;

import at.letto.plugins.config.Endpoint;
import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.dto.JavascriptLibrary;
import at.letto.plugins.dto.PluginConfigDto;
import at.letto.plugins.dto.PluginConfigurationConnection;
import at.letto.plugins.tools.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

/**
 * Liefert den Konfigurationsdialog eines Plugins im Mode CONFIGMODE_URL als HTML-Seite,
 * welche von LeTTo in einem iframe oder Browser-Tab geöffnet wird.<br>
 * Die Seite bindet die JavaScript-Libraries des Plugins ein und ruft die Konfigurations-Methode
 * des Plugins mit dem PluginConfigDto der Konfigurations-Verbindung auf. Änderungen der Konfiguration
 * werden vom Plugin-JavaScript über den reloadPluginDto-Endpoint an die Verbindung übertragen,
 * von wo LeTTo sie über getConfiguration abholt.
 */
@RestController
@Tag(name = "Config Http Controller",
        description = "Konfigurationsdialog des Plugins im iframe-Modus (CONFIGMODE_URL) " +
                "[JavaDoc](" + Endpoint.open + "/javadoc/at/letto/plugins/controller/ConfigHttpController.html)"
)
public class ConfigHttpController {

    /**
     * Verwaltung der registrierten Plugins und Konfigurationssitzungen.
     */
    @Autowired private PluginConfiguration pluginConfiguration;

    /**
     * Liefert die HTML-Seite des Konfigurationsdialogs.
     *
     * @param typ             Typ des Plugins
     * @param configurationID ID der zuvor mit configurationInfo angelegten Konfigurations-Verbindung
     * @return HTML-Seite des Dialogs oder 404, wenn keine gültige Verbindung existiert
     */
    @GetMapping(value = Endpoint.iframeConfig, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> configHttp(@RequestParam String typ, @RequestParam String configurationID) {
        PluginConfigurationConnection conn = pluginConfiguration.getConfigurationConnection(typ, configurationID);
        if (conn == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Konfigurations-Verbindung nicht gefunden oder abgelaufen");
        String jsMethode = conn.getPluginConfigurationInfoDto().getJavaScriptMethode();
        if (jsMethode == null || !jsMethode.matches("^[A-Za-z_$][A-Za-z0-9_$]*$"))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Plugin hat keine gültige Konfigurations-Methode");

        PluginConfigDto dto = conn.getPluginConfigDto();
        if (dto.getPluginDtoUri() == null || dto.getPluginDtoUri().isEmpty()) {
            dto.setPluginDtoUri(pluginConfiguration.getUriGetPluginDto());
            dto.setPluginDtoToken("");
        }
        if (dto.getConfigurationID() == null || dto.getConfigurationID().isEmpty())
            dto.setConfigurationID(conn.getConfigurationID());

        StringBuilder libs = new StringBuilder();
        for (JavascriptLibrary lib : conn.getPluginService().javascriptLibrariesLocal())
            libs.append("<script>\n").append(lib.getJs_code().replace("</script", "<\\/script")).append("\n</script>\n");

        // "<" maskieren, damit der JSON-Inhalt den Script-Block nicht beenden kann
        String dtoJson = JSON.objToJson(dto).replace("<", "\\u003c");
        String config  = conn.getConfig() == null ? "" : conn.getConfig();

        String html = "<!DOCTYPE html>\n" +
                "<html>\n<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Plugin-Konfiguration " + HtmlUtils.htmlEscape(typ) + "</title>\n" +
                "<script src=\"" + Endpoint.open + "/js/jquery-3.6.0.min.js\"></script>\n" +
                libs +
                "</head>\n<body>\n" +
                "<div id=\"configform_div\"></div>\n" +
                "<input type=\"hidden\" class=\"configform_config\" value=\"" + HtmlUtils.htmlEscape(config) + "\">\n" +
                "<script type=\"application/json\" id=\"pluginConfigDto\">" + dtoJson + "</script>\n" +
                "<script>" + jsMethode + "(document.getElementById('pluginConfigDto').textContent);</script>\n" +
                "</body>\n</html>\n";
        return ResponseEntity.ok(html);
    }

}
