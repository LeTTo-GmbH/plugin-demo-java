package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für die Verarbeitung eines Angabetextes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginAngabeRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Angabetext der Frage oder Text in PI-Tags */
    private String text="";

    /** Frage wo das Plugin eingebettet ist */
    private PluginQuestionDto q;

}
