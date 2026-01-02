package at.letto.plugin.controller;

import at.letto.plugin.config.Endpoint;
import at.letto.plugin.config.PluginConfiguration;
import at.letto.plugin.dto.LoadPluginRequestDto;
import at.letto.plugin.dto.PluginDto;
import at.letto.plugin.dto.PluginGeneralInfo;
import at.letto.plugin.dto.PluginGeneralInfoList;
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

    @Autowired private PluginConfiguration pluginConfiguration;
    @Autowired private ApiController apiController;

    /** liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden */
    @GetMapping(Endpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        return apiController.pluginList();
    }

    /** liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services<br>
     * @return [PluginGeneralInfoList](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfoList.html)
     * */
    @GetMapping(Endpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        return apiController.pluginGeneralInfoList();
    }

    /** liefert die allgemeinen Konfigurationsinformationen zu einem Plugin<br>
     *  Body: String - Name des Plugins<br>
     *  @return  [PluginGeneralInfo](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfo.html)
     *  */
    @PostMapping(Endpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(@RequestBody String plugintyp) {
        return apiController.pluginGeneralInfo(plugintyp);
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
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
