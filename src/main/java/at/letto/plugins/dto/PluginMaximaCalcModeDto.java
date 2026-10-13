package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Legt fest, ob Maxima verwendet und ob Datensatzwerte vor der Berechnung eingesetzt werden.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginMaximaCalcModeDto {

    /** true bei Berechnung mit Maxima, false bei Berechnung mit Parser */
    public boolean maxima=false;

    /** true wenn die Datensätze vor Maxima eingesetzt werden */
    public boolean preCalc=true;

}
