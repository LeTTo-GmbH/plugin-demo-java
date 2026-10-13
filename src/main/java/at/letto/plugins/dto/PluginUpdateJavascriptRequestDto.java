package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für die Aktualisierung einer Plugin-Definition anhand der JavaScript-Eingabe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginUpdateJavascriptRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** akt. Plugin-Definition */
    private String pluginDef="";

    /** Rückgabe von JavaScript */
    private String jsResult;

}
