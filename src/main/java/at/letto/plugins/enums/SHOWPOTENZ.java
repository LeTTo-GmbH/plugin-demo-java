package at.letto.plugins.enums;

/**
 * Gibt an, ob eine Potenz als Potenz oder als Wurzel dargestellt werden soll<br>
 * AUTO : Die Darstellung wird automatisch festgelegt<br>
 * POW  : Potenzen werden als Potenz dargestellt<br>
 * SQRT : Wenn es sinnvoll ist, werden Potenzen als Wurzel dargestellt<br>
 */
public enum SHOWPOTENZ{
    /**
     * Automatische Wahl der Potenzdarstellung.
     */
    AUTO,
    /**
     * Darstellung als Potenz.
     */
    POW,
    /**
     * Darstellung als Wurzel, wenn möglich.
     */
    SQRT;
    /**
     * Liest den Darstellungsmodus ohne Beachtung der Groß- und Kleinschreibung.
     *
     * @param s Name des Darstellungsmodus
     * @return Darstellungsmodus oder {@code null}, wenn der Name unbekannt ist
     */
    public static SHOWPOTENZ parse(String s) {
        for (SHOWPOTENZ o:SHOWPOTENZ.values()) {
            if (o.toString().length()>0 && o.toString().equalsIgnoreCase(s)) return o;
        }
        return null;
    }
}
