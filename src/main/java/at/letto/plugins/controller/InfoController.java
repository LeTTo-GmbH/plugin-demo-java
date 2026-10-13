package at.letto.plugins.controller;

import at.letto.plugins.config.Endpoint;
import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.dto.ServiceInfoDTO;
import at.letto.plugins.tools.ServerStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-Endpunkte zur Erreichbarkeit, Version und Laufzeitinformation des Plugin-Services.
 */
@RestController
public class InfoController {

    /**
     * Verwaltung der registrierten Plugins und Konfigurationssitzungen.
     */
    @Autowired private PluginConfiguration pluginConfiguration;

    /**
     * Bestätigt die Erreichbarkeit des internen Endpunkts.
     *
     * @return HTTP-Antwort mit der Erreichbarkeitsmeldung
     */
    @GetMapping(Endpoint.ping)
    public ResponseEntity<String> ping()  {
        return ResponseEntity.ok("pong");
    }

    /**
     * Bestätigt die Erreichbarkeit des öffentlichen Endpunkts.
     *
     * @return HTTP-Antwort mit der Erreichbarkeitsmeldung
     */
    @GetMapping(Endpoint.pingopen)
    public ResponseEntity<String> pingopen()  {
        return ResponseEntity.ok("pong");
    }

    /**
     * Liefert Informationen über den laufenden Plugin-Service.
     *
     * @return HTTP-Antwort mit Service-Informationen
     */
    @GetMapping(Endpoint.INFO)
    public ResponseEntity<ServiceInfoDTO> info()  {
        ServiceInfoDTO serviceInfoDTO = pluginConfiguration.getServiceInfoDto();
        return ResponseEntity.ok(serviceInfoDTO);
    }

    /**
     * Liefert Service-Informationen über den öffentlichen Endpunkt.
     *
     * @return HTTP-Antwort mit Service-Informationen
     */
    @GetMapping(Endpoint.INFO_OPEN)
    public ResponseEntity<ServiceInfoDTO> infoOpen()  {
        ServiceInfoDTO serviceInfoDTO = pluginConfiguration.getServiceInfoDto();
        return ResponseEntity.ok(serviceInfoDTO);
    }

    /**
     * Liefert die Version des Plugin-Services.
     *
     * @return HTTP-Antwort mit der Service-Version
     */
    @GetMapping(Endpoint.VERSION)
    public ResponseEntity<String> version()  {
        return ResponseEntity.ok(PluginConfiguration.PLUGIN_VERSION);
    }

}
