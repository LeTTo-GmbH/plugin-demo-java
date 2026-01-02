package at.letto.plugins.controller;

import at.letto.plugins.config.Endpoint;
import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.dto.ServiceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @Autowired private PluginConfiguration pluginConfiguration;

    @RequestMapping(Endpoint.ping)
    public ResponseEntity<String> ping()  {
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(Endpoint.pingopen)
    public ResponseEntity<String> pingopen()  {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(Endpoint.INFO)
    public ResponseEntity<ServiceInfoDTO> info()  {
        ServiceInfoDTO serviceInfoDTO = pluginConfiguration.getServiceInfoDto();
        return ResponseEntity.ok(serviceInfoDTO);
    }

    @GetMapping(Endpoint.INFO_OPEN)
    public ResponseEntity<ServiceInfoDTO> infoOpen()  {
        ServiceInfoDTO serviceInfoDTO = pluginConfiguration.getServiceInfoDto();
        return ResponseEntity.ok(serviceInfoDTO);
    }

}
