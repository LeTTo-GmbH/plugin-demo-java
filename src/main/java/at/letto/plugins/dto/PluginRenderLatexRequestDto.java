package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anfragedaten für die LaTeX-Ausgabe eines Plugins.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRenderLatexRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Das PluginDto welches am Webserver an das JavaScript des Plugins zum Rendern gesandt wird */
    private PluginDto pluginDto;

    /** Inhalt des Antwortfeldes welches der Schüler eingegeben hat */
    private String answer;

    /** Druckmode.<br>
     *  Der LeTTo-Server übergibt hier derzeit {@code TexPrintMode.toString()}; da diese Klasse kein eigenes toString() hat,
     *  ist der Wert nur ein Objekt-Bezeichner (z.B. {@code at.letto.tools.tex.TexPrintMode@6d06d69c}) und kann vom Plugin nicht ausgewertet werden. */
    private String mode;

}
