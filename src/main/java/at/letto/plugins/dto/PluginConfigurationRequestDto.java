package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für das Abfragen einer Plugin-Konfiguration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginConfigurationRequestDto {

    /** Typ des Plugins */
    private String typ;

    /** eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird */
    private String configurationID="";

}
