package at.letto.plugins.enums;

/**
 * Art der Berechnung<br>
 * MAXIMA  : Berechnung im Maxima-Feld einer Frage<br>
 * LOESUNG : Berechnung der Lösung aus dem Lösungsfeld<br>
 * EQUALS  : Vergleich des Ergebnisses des Schülers mit dem Ergebnis des Lehrers<br>
 * VIEW    : Berechnungen direkt mit Angabefeld mit {=
 */
public enum CALCMODE{
    /**
     * Berechnung im Maxima-Feld einer Frage.
     */
    MAXIMA,
    /**
     * Berechnung der Musterlösung.
     */
    LOESUNG,
    /**
     * Vergleich von Schülerantwort und Musterlösung.
     */
    EQUALS,
    /**
     * Berechnung eines Ausdrucks innerhalb des Angabetextes.
     */
    VIEW
}
