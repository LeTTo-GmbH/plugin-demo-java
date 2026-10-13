package at.letto.plugins.tools;

/**
 * LaTeX-Farbdefinitionen für Aufgaben, Antworten und Bewertungsstatus.
 */
public class Tex {

    /**
     * Farbe und Schriftgröße der Legende im Textkopf.
     */
    public static final String ColorTestLegende = "{\\small\\color{lightgray}";

    /** Farbe der Lösung beim Fragedruck */
    public static final String ColorLoesung = "{\\color{blue}";

    /** Farbe der Schülerantwort beim Fragedruck */
    public static final String ColorAntwort = "{\\color{cyan}";

    /** Farbe des Feedback beim Fragedruck */
    public static final String ColorFeedback = "{\\color{blue}";

    /** Farbe der Question Info beim Fragedruck */
    public static final String ColorQuestionInfo = "{\\small\\color{colorinfo}";

    /** Farbe einer richtigen Antwort beim Fragedruck */
    public static final String ColorAntwortRichtig = "\\colorbox{colorok}{";

    /**
     * Hintergrundfarbe einer falschen Antwort beim Fragedruck.
     */
    public static final String ColorAntwortFalsch = "\\colorbox{colorfalsch}{";

    /** Farbe einer richtigen Antwort beim Fragedruck */
    public static final String ColorAntwortTextRichtig = "{\\color{coloroktext}";

    /**
     * Textfarbe einer falschen Antwort beim Fragedruck.
     */
    public static final String ColorAntwortTextFalsch = "{\\color{colorfalsch}";

    /**
     * Textfarbe einer teilweise richtigen Antwort beim Fragedruck.
     */
    public static final String ColorAntwortTextTeilRichtig = "{\\color{colortextteilrichtig}";

    /**
     * Hintergrundfarbe einer teilweise richtigen Antwort beim Fragedruck.
     */
    public static final String ColorAntwortTeilrichtig = "\\colorbox{colorteilrichtig}{";

    /**
     * Hintergrundfarbe bei einem Angabe- oder Berechnungsfehler.
     */
    public static final String ColorAntwortAngabeFehler = "\\colorbox{colorangabefehler}{";

    /**
     * Hintergrundfarbe einer noch nicht entschiedenen Bewertung.
     */
    public static final String ColorAntwortNichtentschieden = "\\colorbox{lightgray}{";

}
