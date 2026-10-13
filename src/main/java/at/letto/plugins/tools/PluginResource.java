package at.letto.plugins.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * statische Methoden um aus den Systemresourcen der Plugins Dateien zu laden
 */
public class PluginResource {

    /**
     * Gibt einen InputStream auf die gewünschte Ressource zurück
     *
     * @param  resource  Pfad der Ressource (innerhalb von src/main/resources)
     * @return InputStream auf die Ressource
     *
     */
    public static InputStream getResourceAsStream(String resource) {
        return PluginResource.class.getResourceAsStream("/"+resource);
    }

    /**
     * Gibt eine URL auf die gewünschte Ressource zurück
     *
     * @param resource  Pfad der Ressource (innerhalb von src/main/resources)
     * @return URL auf die Ressource
     *
     */
    private static URL getResource(String resource) {
        return PluginResource.class.getResource("/"+resource);
    }

    /**
     * Liest eine Textdatei aus den Ressourcen in einen Vector ein
     *
     * @param resource  Ressourcenpfad relativ zum Klassenpfad
     * @return          Dateiinhalt der Ressource als String-Vector
     *
     */
    public static Vector<String> readResourceFile(String resource){
        try {
            InputStream res = getResourceAsStream(resource);
            Charset charset = Charset.forName("UTF-8");
            // Bufferer - Reader mit entsprechendem Zeichensatz erzeugen
            BufferedReader datei = new BufferedReader(new InputStreamReader(res, charset));
            Vector<String> ret = new Vector<>();
            int lineNr = 0;
            String s;
            while ((s = datei.readLine()) != null)
                ret.add(s);
            datei.close();
            res.close();
            return ret;
        } catch (Exception e) {
            System.out.println("Resource "+resource+" not found!");
        }
        return new Vector<String>();
    }

    /**
     * Liest eine UTF-8-Textressource und verbindet ihre Zeilen mit Zeilenumbrüchen.
     *
     * @param resource Ressourcenpfad relativ zum Klassenpfad
     * @return Ressourceninhalt oder leerer String, wenn die Ressource nicht gelesen werden kann
     */
    public static String readResourceFileString(String resource) {
        StringBuilder sb = new StringBuilder();
        for (String s:readResourceFile(resource)) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Ersetzt deutsche Umlaute und das Eszett durch die im Plugin hinterlegten HTML-Ersetzungen.
     *
     * @param data zu verarbeitende Daten
     * @return Text beziehungsweise Textzeilen mit den ersetzten Zeichen
     */
    public static Vector<String> replaceUmlautHTML(Vector<String> data) {
        Vector<String> ret = new Vector<String>();
        for (String line:data)
            ret.add(replaceUmlautHTML(line));
        return ret;
    }

    /**
     * Ersetzt deutsche Umlaute und das Eszett durch die im Plugin hinterlegten HTML-Ersetzungen.
     *
     * @param data zu verarbeitende Daten
     * @return Text beziehungsweise Textzeilen mit den ersetzten Zeichen
     */
    public static String replaceUmlautHTML(String data) {
        data = data.replaceAll("ä","&auml").replaceAll("ü","&uuml;").replaceAll("ö","&ouml;");
        data = data.replaceAll("Ä","&Auml").replaceAll("Ü","&Uuml;").replaceAll("Ö","&Ouml;");
        data = data.replaceAll("ß","&suml");
        data = data.replaceAll("ä","&auml").replaceAll("ü","&uuml;").replaceAll("ö","&ouml;");
        data = data.replaceAll("Ä","&Auml").replaceAll("Ü","&Uuml;").replaceAll("Ö","&Ouml;");
        data = data.replaceAll("ß","&suml");
        return data;
    }

}
