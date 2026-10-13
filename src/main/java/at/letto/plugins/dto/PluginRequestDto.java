package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für die Auswahl eines Plugins anhand von Typ, Name und Konfiguration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Parameter für die Antworterzeugung */
    private String params="";

    /** Frage wo das Plugin eingebettet ist */
    private PluginQuestionDto q;

    /** Art der Berechnung */
    private PluginMaximaCalcModeDto pluginMaximaCalcMode;

}
