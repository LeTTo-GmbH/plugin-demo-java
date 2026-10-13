package at.letto.plugins.controller;

import at.letto.plugins.config.Endpoint;
import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.dto.LoadPluginRequestDto;
import at.letto.plugins.dto.PluginDto;
import at.letto.plugins.dto.PluginGeneralInfo;
import at.letto.plugins.dto.PluginGeneralInfoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * offene Endpoints welche von extern erreichbar sein müssen für ajax und
 * allgemeine Informationen (von extern erreichbar)
 */
@RestController
@RequestMapping(Endpoint.EXTERN_OPEN)
public class ApiExternOpenController {

    /**
     * Verwaltung der registrierten Plugins und Konfigurationssitzungen.
     */
    @Autowired private PluginConfiguration pluginConfiguration;
    /**
     * Interner Controller für delegierte Plugin-Anfragen.
     */
    @Autowired private ApiController apiController;

    /**
     * liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden
     *
     * @return HTTP-Antwort mit den Namen der Plugin-Typen
     */
    @GetMapping(Endpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        return apiController.pluginList();
    }

    /** liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services<br>
     * @return HTTP-Antwort mit den allgemeinen Informationen aller registrierten Plugins
     * */
    @GetMapping(Endpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        return apiController.pluginGeneralInfoList();
    }

    /**
     * liefert die allgemeinen Konfigurationsinformationen zu einem Plugin<br>
     * Body: String - Name des Plugins<br>
     *
     * @return HTTP-Antwort mit den allgemeinen Informationen des angefragten Plugins
     *
     * @param plugintyp Name des registrierten Plugin-Typs
     */
    @PostMapping(Endpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(@RequestBody String plugintyp) {
        return apiController.pluginGeneralInfo(plugintyp);
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren JavaScript - Bearbeitung
     * @param r    LoadPluginRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.params    String: Plugin-Parameter<br>
     *   r.nr        int: Laufende Nummer für alle PIG-Tags und Question-Plugins<br>
     *   r.configurationID String:ID der aktuellen Konfiguration
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @PostMapping(Endpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.reloadPluginDto(r);
    }

}
