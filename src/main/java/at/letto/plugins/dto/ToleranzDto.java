package at.letto.plugins.dto;

import at.letto.plugins.enums.TOLERANZMODE;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Beschreibt Wert und Modus der Toleranz bei der Antwortbewertung.
 */
@Data
@AllArgsConstructor
public class ToleranzDto implements Cloneable {

    /** Wert der Toleranz als Absolutwert, oder als relativer Wert (bei 1Prozent ist toleranz=0.01) */
    private double toleranz;

    /** RELATIV : Die Toleranz wird relativ zum größeren Wert des Vergleiches bestimmt<br>
     *  ABSOLUT : Die Toleranz wird absolut angegeben und ist von den Werten unabhängig  */
    private TOLERANZMODE mode;

    /**
     * Erstellt eine relative Toleranz mit dem Standardwert {@code 1e-10}.
     */
    public ToleranzDto() {
        this.toleranz = 1e-10;
        this.mode = TOLERANZMODE.RELATIV;
    }

    /**
     * Prüft, ob die Toleranz relativ ausgewertet wird.
     *
     * @return {@code true} bei relativem Toleranzmodus
     */
    public boolean relativ() {
        return this.mode == TOLERANZMODE.RELATIV;
    }

    /**
     * Prüft, ob die Toleranz absolut ausgewertet wird.
     *
     * @return {@code true} bei absolutem Toleranzmodus
     */
    public boolean absolut() {
        return this.mode == TOLERANZMODE.ABSOLUT;
    }

}
