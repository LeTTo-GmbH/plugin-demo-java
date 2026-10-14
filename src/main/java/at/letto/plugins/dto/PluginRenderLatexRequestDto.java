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

    /** Druckmode, vom LeTTo-Server als {@code TexPrintMode.toString()} übergeben, z.B.
     *  {@code PRINTFRAGE;datasets=0,1;recurse=false;newPage=false;line=false}.<br>
     *  Der erste Teil ist der Druckmodus: PRINTFRAGE, PRINTERGEBNIS, PRINTFORMEL, PRINTINFO, PRINTHTML, PRINTANTWORT,
     *  PRINTANTWORTERGEBNIS oder PRINTEINSICHTNAHME (z.B. {@code mode.split(";")[0]}); danach folgen die zu druckenden
     *  Datensätze, rekursiver Druck, neue Seite und Linie nach jeder Frage.<br>
     *  Ältere LeTTo-Server übergeben nur einen nicht auswertbaren Objekt-Bezeichner (z.B. {@code at.letto.tools.tex.TexPrintMode@6d06d69c}). */
    private String mode;

}
