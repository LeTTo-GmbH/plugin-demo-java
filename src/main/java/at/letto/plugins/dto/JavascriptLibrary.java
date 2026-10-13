package at.letto.plugins.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Beschreibt eine JavaScript-Bibliothek als lokale Ressource, Serverdatei oder Quelltext.
 */
@Getter @Setter
@NoArgsConstructor
public class JavascriptLibrary {

    /** JS-Bibliothek local als Ressource verfügbar */
    public static final String LOCAL="LOCAL";
    /** JS-Bibliothek von einem Server mit vollständiger Adresse verfügbar */
    public static final String SERVER="SERVER";
    /** JS-Library als vollständiger Text verfügbar */
    public static final String JAVASCRIPT="JAVASCRIPT";

    /**
     * Ressourcenverzeichnis oder Serveradresse der JavaScript-Bibliothek.
     */
    private String library;
    /** Name / Bezeichner der Bibliothek */
    private String name;
    /** relativer Pfad am JS-Server ohne Server-Adresse bei glob. JS-Libraries */
    private String globalName;
    /** Definition, wie JS im Template eingebaut wird (lokal, server, text) */
    private String local = LOCAL;
    /** Vollständiger JS-Code im Text-Modus */
    private String js_code = "";


    /**
     * Definition einer lokalen JS-Library, wird von JSF aus Ressourcen verwaltet
     * @param path Pfad der Library im Ressourcen-Verzeichnis
     */
    public JavascriptLibrary(String path) {
        String libPath = path.replaceAll("(^[^\\/]+)(.*)", "$1");
        String libName = path.replaceAll(libPath + "/", "");

        this.library = libPath;
        this.name = libName;
        this.local = LOCAL;
    }

    /**
     * JS-Library mit kompletten Quelltext
     *
     * @param name Name der JavaScript-Bibliothek
     * @param jscode    Vollständiger JS-Code
     * @param code Unterscheidungsparameter dieser Konstruktorvariante; der Wert wird nicht ausgewertet
     */
    public JavascriptLibrary(String name, String jscode, boolean code) {
        String libPath = "";
        String libName = name;

        this.library = "";
        this.name = name;
        this.js_code = jscode;
        this.local = JAVASCRIPT;
    }

    /**
     * Definition einer externen JS-Bibliothek
     *
     * @param server Server-Pfad, wo die JSF-Dateien liegen
     * @param path  unterordner + JS-File + Params
     */
    public JavascriptLibrary(String server, String path) {
        this.local = SERVER;
        this.library = server;
        this.name = path;
        this.globalName = path;
    }

    /**
     * Beschreibt eine Serverbibliothek mit getrennten lokalen und globalen Dateipfaden.
     *
     * @param server Server-Pfad, wo die JSF-Dateien liegen
     * @param pathlocal  Unterordner, JavaScript-Datei und Parameter, wenn JS-Library lokal verfügbar
     * @param pathGlobal Unterordner, JavaScript-Datei und Parameter für Zugriff über Web auf global verfügbare JS-Bibliotheken
     *
     */
    public JavascriptLibrary(String server, String pathlocal, String pathGlobal) {
        this.local = SERVER;
        this.library = server;
        this.name = pathlocal;
        this.globalName = pathGlobal;
    }

    /**
     * Definition der Pfadangabe, von wo JS-Libraries bezogen werden
     *
     * @param localServer   Webadresse des Hauptservers im LOKALEN Modus
     * @return  Pfad im Modus LOKAL, GLOBAL
     *
     */
    public String loadPath(String localServer) {
        if (local.equals(LOCAL)) return localServer + "" + name;
        if (local.equals(SERVER)) return library + globalName;
        return "";
    }

    /**
     * Vergleicht Bezugsmodus, Name und gegebenenfalls Serverpfad zweier Bibliotheken.
     *
     * @param lib zu vergleichende Bibliothek
     * @return {@code true}, wenn die verwendeten Bibliotheksangaben übereinstimmen
     */
    public boolean equals(JavascriptLibrary lib) {
        if (!local.equals(lib.local)) return false;
        switch (local) {
            default:
            case JAVASCRIPT:
            case LOCAL  : return name!=null?name.equals(lib.name):(name==lib.name);
            case SERVER : return (name!=null?name.equals(lib.name):(name==lib.name)) &&
                                 (library!=null?library.equals((lib.library)):(library==lib.library)) &&
                                 (globalName!=null?globalName.equals((lib.globalName)):(globalName==lib.globalName));
        }
    }

}