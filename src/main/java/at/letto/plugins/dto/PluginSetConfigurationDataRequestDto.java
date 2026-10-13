package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für die Übermittlung von Konfigurationsdaten.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginSetConfigurationDataRequestDto {

    /** Typ des Plugins */
    private String typ;

    /** eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird */
    private String configurationID="";

    /** aktueller Konfigurations-String des Plugins */
    private String configuration;

    /** Question-DTO mit Varhashes */
    private PluginQuestionDto questionDto;

}
