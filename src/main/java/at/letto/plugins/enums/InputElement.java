package at.letto.plugins.enums;

/**
 * Definiert das Eingabeelement für die Subquestion, deren Typ
 * auf Plugin gesetzt ist.
 * Mögliche Eingabetypen:
 * @author Thomas Mayer
 *
 */
public enum InputElement {
    /**
     * TextField für ein Textfeld
     */
    TextField,
    /**
     * TextArea für einen größeren Eingabebereich
     */
    TextArea,
    /**
     * Schaltfläche als Eingabeelement.
     */
    Button,
    /**
     * Auswahl mehrerer vorgegebener Antworten.
     */
    MULTICHOICE,
    /**
     * Eingabeelement mit XHTML-Inhalt.
     */
    XHTML,
    /**
     * JavaScript-Editor für Eingabe von Quelltext von Programmen
     */
    SourceCode,
    /**
     * Interaktive JavaScript-Eingabe.
     */
    JAVASCRIPT
}
