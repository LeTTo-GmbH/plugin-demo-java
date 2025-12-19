package at.letto.plugindemojava.config;

import at.letto.plugindemojava.service.PluginService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Wird beim Systemstart nach den Constructoren der Services geladen und darf dann in keinem Service injiziert werden!!!
 * Wird gestartet bevor der die Filter-Chain des Webservers und der TomEE gestartet werden!
 */
@Configuration
public class StartupConfiguration {

    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);


    private final PluginService      pluginService;
    private final ApplicationContext  applicationContext;
    private final PluginConfiguration pluginConfiguration;

    public StartupConfiguration(PluginService pluginService, ApplicationContext applicationContext, PluginConfiguration pluginConfiguration) {
        this.pluginService = pluginService;
        this.applicationContext = applicationContext;
        this.pluginConfiguration = pluginConfiguration;

        pluginConfiguration.init();

        // Nun wird das Service am Setup registriert
        this.pluginService.registerPlugin();
    }
}
