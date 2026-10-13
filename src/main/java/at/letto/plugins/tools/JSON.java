package at.letto.plugins.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Umwandlung Object - JSON-String
 */
public class JSON {

    // First, definition of what to escape
    /**
     * Jackson-Zeichenmaskierung mit zusätzlicher Maskierung des Apostrophs.
     */
    public static class HTMLCharacterEscapes extends CharacterEscapes
    {
        /**
         * Maskierungsregeln für ASCII-Zeichen.
         */
        private final int[] asciiEscapes;

        /**
         * Initialisiert die JSON-Maskierungstabelle und ergänzt die Apostrophmaskierung.
         */
        public HTMLCharacterEscapes()
        {
            // start with set of characters known to require escaping (double-quote, backslash etc)
            int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
            // and force escaping of a few others:
//            esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
//            esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
//            esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
            esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
            asciiEscapes = esc;
        }
        // this method gets called for character codes 0 - 127
        /**
         * Liefert die Maskierungsregeln für ASCII-Zeichen.
         *
         * @return ASCII-Maskierungstabelle
         */
        @Override public int[] getEscapeCodesForAscii() {
            return asciiEscapes;
        }
        // and this for others; we don't need anything special here
        /**
         * Liefert keine zusätzliche benutzerdefinierte Maskierung außerhalb der ASCII-Tabelle.
         *
         * @param ch Unicode-Codepunkt
         * @return {@code null}, da keine zusätzliche Maskierungssequenz definiert ist
         */
        @Override public SerializableString getEscapeSequence(int ch) {
            // no further escaping (beyond ASCII chars) needed:
            return null;
        }
    }
    /**
     * Gemeinsam verwendeter Jackson-Mapper, bei Bedarf initialisiert.
     */
    private static ObjectMapper mapper = null;

    /**
     * Initialisiert bei Bedarf den gemeinsam verwendeten Jackson-Mapper.
     *
     * @return Mapper mit der konfigurierten Zeichenmaskierung
     */
    private static ObjectMapper getMapper() {
        if (mapper!=null) return mapper;
        mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
        return mapper;
    }

    /**
     * JSON-String in Objekt umwandeln
     * @param jsonfile  json-File mit jsonString
     * @param tClass    Ziel-Klasse
     * @param <T>       KlassenTyp
     * @return          Eingelesenes Objekt
     */
    public static <T> T jsonToObj(final File jsonfile, Class<T> tClass) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String line: Files.readAllLines(jsonfile.toPath()))
                sb.append(line+"\n");
            String json = sb.toString();
            return jsonToObj(json,tClass);
        } catch (IOException e) {
            System.out.println("JSONfile "+jsonfile.getAbsolutePath()+" kann nicht gelesen werden ");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JSON-String in Objekt umwandeln
     *
     * @param json      JSON-Text
     * @param tClass    Ziel-Klasse
     * @param <T>       KlassenTyp
     * @return          Eingelesenes Objekt
     *
     */
    public static <T> T jsonToObj(final String json, Class<T> tClass) {
        try {
            T erg = getMapper().readValue(json, tClass);
            return erg;
        } catch (JsonProcessingException e) {
//            System.out.println("JSON kann nicht in Klasse "+tClass.toString()+" geparst werden!!");
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Liest ein Objekt aus JSON.
     *
     * @param json JSON-Text
     * @param typeRef Typbeschreibung des Zielobjekts einschließlich generischer Typen
     * @param <T> Typ des verarbeiteten Objekts
     * @return eingelesenes Objekt oder {@code null}, wenn das Lesen oder Umwandeln fehlschlägt
     */
    public static <T> T jsonToObj(final String json, TypeReference<T> typeRef) {
        try {
            T erg = getMapper().readValue(json, typeRef);
            return erg;
        } catch (JsonProcessingException e) {
//            System.out.println("JSON kann nicht in Klasse "+typeRef.getType().toString()+" geparst werden!!");
            // e.printStackTrace();
            return null;
        }
    }

    /**
     * JSON-String in Objekt umwandeln
     *
     * @param json      JSON-Text
     * @param tClass    Ziel-Klasse als String des Klassennamens
     * @return          Eingelesenes Objekt
     *
     */
    public static Object jsonToObj(final String json, String tClass) {
        try {
            Object erg = getMapper().readValue(json, Class.forName(tClass));
            return erg;
        } catch (JsonProcessingException | ClassNotFoundException e) {
//            System.out.println("JSON kann nicht in Klasse "+tClass+" geparst werden!!");
            // e.printStackTrace();
            return null;
        }
    }


    /**
     * Objekt in json-String umwandeln
     * @param obj   umzuwandelndes Objekt
     * @return      json-String
     * @throws RuntimeException wenn die JSON-Ausgabe nicht erzeugt oder geschrieben werden kann
     */
    public static String objToJson(final Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Objekt in json-String umwandeln und in eine Datei speichern
     * @param obj   umzuwandelndes Objekt
     * @param file  Zieldatei
     * @throws RuntimeException wenn die JSON-Ausgabe nicht erzeugt oder geschrieben werden kann
     */
    public static void objToJson(final Object obj, File file) {
        try {
            file.createNewFile();
            getMapper().writeValue(file, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
