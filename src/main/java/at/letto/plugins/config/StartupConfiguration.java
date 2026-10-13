package at.letto.plugins.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Initialisiert beim Aufbau des Spring-Anwendungskontexts die Plugin-Verwaltung und registriert die Plugin-Typen.
 * Die Registrierung am Setup-Service erfolgt separat nach dem Anwendungsstart.
 */
@Configuration
public class StartupConfiguration {

    /**
     * Logger für Meldungen dieser Klasse.
     */
    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

    /**
     * Spring-Anwendungskontext.
     */
    private final ApplicationContext  applicationContext;
    /**
     * Verwaltung der registrierten Plugins und Konfigurationssitzungen.
     */
    private final PluginConfiguration pluginConfiguration;

    /**
     * Initialisiert die Plugin-Verwaltung und registriert das Uhr-Demo-Plugin.
     *
     * @param applicationContext Spring-Anwendungskontext
     * @param pluginConfiguration Verwaltung der Plugin-Konfiguration
     */
    public StartupConfiguration(ApplicationContext applicationContext, PluginConfiguration pluginConfiguration) {
        this.applicationContext = applicationContext;
        this.pluginConfiguration = pluginConfiguration;

        //Initialisierung der Plugin-Konfiguration
        pluginConfiguration.init();

        // Registrierung der Plugins
        pluginConfiguration.registerPlugin("Uhr1", "at.letto.plugins.plugin.uhr.PluginUhr");

        // Nun wird das Plugin-Service am Setup registriert
        // hier bitte nicht: pluginConfiguration.registerPluginInSetup();
    }

}
