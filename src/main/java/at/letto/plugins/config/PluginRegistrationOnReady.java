package at.letto.plugins.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PluginRegistrationOnReady {

    private final PluginConfiguration pluginConfiguration;

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