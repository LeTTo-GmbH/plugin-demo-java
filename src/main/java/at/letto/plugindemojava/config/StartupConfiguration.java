package at.letto.plugindemojava.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Wird beim Systemstart nach den Constructoren der Services geladen und darf dann in keinem Service injiziert werden!!!
 * Wird gestartet bevor der die Filter-Chain des Webservers und der TomEE gestartet werden!
 */
@Configuration
public class StartupConfiguration {

    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

    private final ApplicationContext  applicationContext;
    private final PluginConfiguration pluginConfiguration;

    public StartupConfiguration(ApplicationContext applicationContext, PluginConfiguration pluginConfiguration) {
        this.applicationContext = applicationContext;
        this.pluginConfiguration = pluginConfiguration;

        //Initialisierung der Plugin-Konfiguration
        pluginConfiguration.init();

        // Registrierung der Plugins
        pluginConfiguration.registerPlugin("Uhr", "at.open.letto.plugins.uhr.PluginUhr");

        // Nun wird das Plugin-Service am Setup registriert
        pluginConfiguration.registerPluginInSetup();
    }

}
