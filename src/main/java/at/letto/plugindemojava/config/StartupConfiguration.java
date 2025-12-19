package at.letto.plugindemojava.config;

import at.letto.plugindemojava.service.PluginService;
import org.springframework.context.annotation.Configuration;

/**
 * Wird beim Systemstart nach den Constructoren der Services geladen und darf dann in keinem Service injiziert werden!!!
 * Wird gestartet bevor der die Filter-Chain des Webservers und der TomEE gestartet werden!
 */
@Configuration
public class StartupConfiguration {

    private final PluginService pluginService;

    public StartupConfiguration(PluginService pluginService) {
        this.pluginService = pluginService;

        // Nun wird das Service am Setup registriert
        pluginService.registerPlugin();
    }
}
