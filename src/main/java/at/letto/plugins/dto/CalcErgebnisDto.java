package at.letto.plugins.dto;

import at.letto.plugins.enums.CALCERGEBNISTYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Speichert ein CalcErgebnis als Dto um über eine Rest- oder Service-Schittstelle zu übertragen.<br>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcErgebnisDto implements Cloneable {

    /** CalcErgebnis als normaler lesbarer String, welcher noch nicht geparst ist */
    private String       string;

    /** JSON-String des CalcErgebnis-Ausdrucks */
    private String       json;

    /** Typ, wenn nicht über json übertragen werden kann */
    private CALCERGEBNISTYPE type;


    /**
     * Liefert eine Textdarstellung dieses Objekts.
     *
     * @return Textdarstellung des Objekts
     */
    @Override
    public String toString() {
        switch (type) {
            case CALCULATE -> {
               return json;
            }
            case STRING -> {
                return string;
            }
            case ERROR -> {
                return "error";
            }
        }
        return "null";
    }

}
