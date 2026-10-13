package at.letto.plugins.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Registriert den Plugin-Service nach dem vollständigen Start der Anwendung am Setup-Service.
 */
@Component
public class PluginRegistrationOnReady {

    /**
     * Verwaltung der registrierten Plugins und Konfigurationssitzungen.
     */
    private final PluginConfiguration pluginConfiguration;

    /**
     * Verknüpft den Registrierungslistener mit der Plugin-Verwaltung.
     *
     * @param pluginConfiguration Verwaltung der Plugin-Konfiguration
     */
    public PluginRegistrationOnReady(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    /**
     * Hier wird das Plugin am Setup-Service registriert
     */
    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        pluginConfiguration.registerPluginInSetup();
    }

}