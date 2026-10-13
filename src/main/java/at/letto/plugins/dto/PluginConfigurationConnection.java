package at.letto.plugins.dto;

import at.letto.plugins.plugin.PluginService;
import at.letto.plugins.tools.Datum;
import at.letto.plugins.tools.RandomString;
import lombok.Getter;

import java.util.HashMap;

/**
 * Speichert Zustand und Gültigkeit einer Plugin-Konfigurationssitzung.
 */
@Getter
public class PluginConfigurationConnection {
    /** Konfigurations ID */
    public final String configurationID;
    /** Typ des Plugins */
    public final String typ;
    /** Name des Plugins */
    public final String name;
    /** Konfigurationsstring des Plugins */
    public String config;
    /** Plugin das gerade bearbeitet wird */
    public PluginService pluginService;
    /** Plugin Konfigurations-Information */
    public PluginConfigurationInfoDto pluginConfigurationInfoDto;
    /** PluginConfigDto welches aktuell gültig ist */
    public PluginConfigDto pluginConfigDto;
    /** PluginQuestionDto der Frage welche zu dem Plugin gehört */
    public PluginQuestionDto pluginQuestionDto;
    /** Gültigkeitsdauer in Sekunden */
    public long timeout;
    /** Zugriffszeitstempel des letzten Zugriffes als DateInteger in Sekunden */
    public long lastTime = Datum.nowDateInteger();

    /**
     * Erstellt eine Konfigurationssitzung mit Plugin-Instanz und Inaktivitätsfrist.
     *
     * @param typ Typ des Plugins
     * @param name Name beziehungsweise Bezeichner
     * @param config Konfigurationsstring des Plugins
     * @param configurationID eindeutige ID der Konfigurationssitzung
     * @param pluginService zugehörige Plugin-Instanz
     * @param timeout maximale Inaktivitätsdauer der Sitzung in Sekunden
     */
    public PluginConfigurationConnection(String typ, String name, String config, String configurationID, PluginService pluginService, long timeout) {
        this.typ    = typ;
        this.name   = name;
        if (configurationID!=null && configurationID.trim().length()>0) {
            this.configurationID = configurationID;
        } else {
            this.configurationID = RandomString.generateRandomString(30);
        }
        this.timeout=timeout;
        this.pluginConfigDto = new PluginConfigDto(
                typ,
                name,
                config,
                "plugintag",
                800,600,  // Größe des Editier-Fensters
                this.configurationID,
                "",// error-Message
                new PluginDto(),
                "",
                "",
                new HashMap<>(),      // Daten welche für LeTTo sichtbar sind
                ""                    // json mit Daten welche von LeTTo nicht ausgewertet werden sollen
        );
        changeConfig(config, pluginService);
    }

    /**
     * Aktualisiert Konfiguration, Plugin-Instanz und Informationen zum Konfigurationsdialog.
     *
     * @param config Konfigurationsstring des Plugins
     * @param pluginService zugehörige Plugin-Instanz
     */
    public void changeConfig(String config, PluginService pluginService) {
        this.config                     = config;
        this.pluginService              = pluginService;
        this.pluginConfigurationInfoDto = pluginService.configurationInfo(configurationID);
    }
}
