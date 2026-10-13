package at.letto.plugins.plugin;

import at.letto.plugins.config.PluginConfiguration;
import at.letto.plugins.dto.*;
import at.letto.plugins.enums.*;
import at.letto.plugins.tools.*;
import at.letto.plugins.dto.JavascriptLibrary;
import lombok.Getter;
import lombok.Setter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gemeinsame Basis für Plugin-Metadaten, Ressourcen, Bilderzeugung und Konfiguration.
 */
@Getter
public abstract class BasePlugin implements PluginService {

    // ---------------------------------------------------------------------------------------------------------------------
    //        Eigenschaften
    // ---------------------------------------------------------------------------------------------------------------------
    /** Name des Elements */
    @Setter protected String name;

    /** Namen der Wiki-Seite wenn eine Doku am LeTTo-Wiki vorliegt */
    protected String wikiHelp="Plugins";

    /** Hilfe-URL für die Beschreibung des Plugins */
    protected String helpUrl="";

    /** Gibt an ob die Standard-Plugin-Configuration verwendet werden soll */
    protected boolean defaultPluginConfig=false;

    /** Konfigurationsstring des Elements */
    protected String config;

    /** Typ des Plugins */
    protected String typ;

    /** Breite des zu erzeugenden Bildes */
    protected int width  = 400;

    /** Höhe des zu erzeugenden Bildes */
    protected int height = 400;

    /** Größe des Bildes in Prozent */
    protected int imageWidthProzent=100;

    /** True wenn das Plugin CalcErgebnis und VarHash als JSON verarbeiten kann */
    protected boolean math = false;

    /** Version des Plugins */
    protected String   version = "1.0";

    /** Plugin Hilfe als HTML für den Plugin - Dialog */
    protected String[] helpfiles = {"plugins/plugin.html"};

    /** JavaScript Libraries für das Plugin */
    protected String[] javascriptLibs = {"plugins/plugintools.js"};

    /** Name der JAVA-Script Methode zur Plugin-Initialisierung für die interaktive Ergebniseingabe */
    protected String initPluginJS="";

    /** gibt an ob das Plugin eine JavaScript Schnittstelle bei der Beispieldarstellung hat */
    protected boolean javaScript=false;

    /** Plugin ist stateless und liefert bei gleicher Angabe immer das gleiche Verhalten */
    protected boolean cacheable = true;

    /** Gibt an ob im Plugin die Frage benötigt wird */
    protected boolean useQuestion=true;

    /** gibt an ob die Datensatz-Variable ohne Konstante benötigt werden */
    protected boolean useVars=true;

    /** gibt an ob die Datensatz-Variable mit Konstanten benötigt werden */
    protected boolean useCVars=true;

    /** gibt an ob die Maxima-Durchrechnungen ohne eingesetzte Datensätze benötigt werden */
    protected boolean useMaximaVars=true;

    /** gibt an ob die Maxima-Durchrechnungen mit eingesetzten Datensätzen benötigt werden */
    protected boolean useMVars=true;

    /** Konfigurations-Mode für die Konfiguration des Plugins */
    protected int configurationMode = PluginConfigurationInfoDto.CONFIGMODE_JSF;

    /** Gibt an, ob im Plugin-Konfig-Dialog Datensätze hinzugefügt werden können.
     * => Button AddDataset in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    protected boolean addDataSet=true;

    /** Gibt an, ob das Plugin über den Browser direkt erreichbar ist */
    protected boolean externUrl=false;

    /** Gibt an ob im Plugin bei der Konfiguration die Maxima-Berechnung durchlaufen werden kann.
     * => Button Maxima in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    protected boolean calcMaxima=true;

    /** Name der JAVA-Script Methode zur Configuration des Plugins */
    protected String  configPluginJS="configPlugin";

    /** URL des Plugin-Services für die direkte Kommunikation*/
    protected String pluginServiceURL="";

    // ---------------------------------------------------------------------------------------------------------------------
    //        Constructor
    // ---------------------------------------------------------------------------------------------------------------------
    /**
     * Erzeugt ein Question Plugin
     * @param name   Name des Plugins
     * @param params Parameter des Plugins
     */
    public BasePlugin(String name, String params) {
        this.name   = name;
        this.config = params;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    //        Methoden
    // ---------------------------------------------------------------------------------------------------------------------
    /**
     * Liefert den Typ des Plugins - Klasse des Plugins.
     *
     * @return Liefert den Typ des Plugins - Klasse des Plugins
     */
    @Override
    public String getPluginType() {
        return this.getClass().toString();
    }

    /**
     * Liefert den String welcher für die Definition des Plugins gespeichert wird [PI name typ "config"].
     *
     * @return Liefert den String welcher für die Definition des Plugins gespeichert wird [PI name typ "config"]
     */
    @Override
    public String getTag() {
        return "[PI "+ name + " " + typ + " \""+config+"\"]";
    }

    /**
     * Liefert die Versionskennung dieses Plugins.
     *
     * @return Versionskennung
     */
    @Override
    public String getPluginVersion(){
        return getVersion();
    }

    /**
     * Stellt die allgemeinen Plugin-Metadaten zusammen.
     *
     * @return Metadaten dieses Plugins
     */
    @Override
    public PluginGeneralInfo getPluginGeneralInfo() {
        PluginGeneralInfo info = new PluginGeneralInfo(
                getTyp(),
                getVersion(),
                getWikiHelp(),
                getHelpUrl(),
                getHelp(),
                isDefaultPluginConfig(),
                isMath(),
                getClass().toString(),
                getInitPluginJS(),
                isJavaScript(),
                javascriptLibraries(),
                javascriptLibrariesLocal(),
                getInputElement().toString(),
                isCacheable(),
                isUseVars(),
                isUseCVars(),
                isUseMaximaVars(),
                isUseMVars(),
                getPluginServiceURL()
        );
        return info;
    }

    /**
     * Liefert eine HTML-Hilfe zu dem Plugin.<br>
     * Die konfigurierten Hilfedateien werden zusammengefügt; ohne Hilfetext wird ein Hinweis auf die fehlende Konfiguration geliefert.
     * @return HTML Hilfe
     */
    @Override
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        String help = "";
        if (this.getHelpfiles()!=null) {
            for (String hf : this.getHelpfiles()) {
                String msg = getHelpMessageFromResource(hf);
                //msg = PluginResource.replaceUmlautHTML(msg);
                sb.append(msg);
            }
            help = sb.toString().trim();
        }
        if (help.length()<1) help = "<h1>Plugin-Template</h1>Help ist noch nicht konfiguriert!";
        return help;
    }

    /**
     * Lädt den HTML-Hilfetext und ersetzt Umlaute durch HTML-Entitäten.
     *
     * @param resource Ressourcenpfad relativ zum Klassenpfad
     * @return aufbereiteter Hilfetext
     */
    protected String loadHelpFile(String resource) {
        return PluginResource.replaceUmlautHTML(getHelpMessageFromResource(resource));
    }

    /**
            * Lädt eine HTML-Hilfedatei aus den Ressourcen
	 * @param   name Pfad der Hilfedate
	 * @return  Inhalt der Hilfedatei als String
	 */
    protected final String getHelpMessageFromResource(String name) {
        try {
            Vector<String> helptext = PluginResource.readResourceFile(name);
            boolean body=false;
            StringBuilder sb = new StringBuilder();
            for (String s:helptext) {
                s = s.replaceAll("\\r","");
                if (!body && s.contains("<body")) {
                    Matcher m;
                    if ((m = Pattern.compile("^.*\\<body[^\\>]*\\>(.*)$").matcher(s)).find()) {
                        sb.append( m.group(1) );
                    }
                    body=true;
                } else if (body) {
                    if (s.contains("</body")) {
                        Matcher m;
                        if ((m = Pattern.compile("^(.*)\\<\\/body[^\\>]*\\>.*$").matcher(s)).find()) {
                            sb.append( m.group(1) );
                        }
                        body=false;
                    }
                    else sb.append( "\n"+s );
                }
            }
            return sb.toString().trim();
        } catch (Exception | Error e) {
            return "Hilfe Resource "+name+" für das Plugin wurde nicht gefunden!";
        }
    }

    /**
     * Liefert einen Angabestring für die MoodleText Angabe und erzeugt gegebenenfalls fehlende Datasets in der Datasetliste
     *
     * @param params Parameter für die Einstellungen
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     */
    @Override
    public String getAngabe(String params) {
        return null;
    }

    /**
     * Liefert die für dieses Plugin benötigten Datensätze.
     *
     * @return Liste der benötigten Datensätze; standardmäßig leer
     */
    @Override
    public List<PluginDatasetDto> generateDatasets() {
        return new ArrayList<PluginDatasetDto>();
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     *
     * @param params Parameter für die Antworterzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return HTML Text
     */
    @Override
    public String getHTML(String params, PluginQuestionDto q) {
        return "";
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     *
     * @param params               Parameter
     * @param q                    Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return Maxima Berechnungs-String
     */
    @Override
    public String getMaxima(String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode) {
        return "";
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     *
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     */
    @Override
    public Vector<String> getVars() {
        return null;
    }

    /**
     * Bestimmt einen eindeutigen String, welcher ein Plugin-Bild beschreibt um daraus den Dateinamen bestimmen zu können.
     * @param imageParams Parameter des PIG-Tags
     * @param q           Frage in der das Plugin eingebettet ist
     * @return            eindeutiger String der das Plugin-Bild eindeutig beschreibt.
     */
    @Override
    public String getPluginImageDescription(String imageParams, PluginQuestionDto q) {
        StringBuilder sb = new StringBuilder();
        sb.append("typ:");
        sb.append(this.typ);
        sb.append(" version:");
        sb.append(this.getPluginVersion());
        sb.append(" name:");
        sb.append(this.getName());
        sb.append(" config:");
        sb.append(this.getConfig());
        sb.append(" params:");
        sb.append(imageParams);
        if (q!=null) {
            sb.append(" question:");
            sb.append(JSON.objToJson(q));
        }
        return sb.toString();
    }

    /**
     * Zeichnet eine Fehlermeldung in ein Ersatzbild.
     *
     * @param msg anzuhängende beziehungsweise darzustellende Meldung
     * @return Fehlerbild mit 500 mal 500 Pixeln
     */
    private BufferedImage errorMessageImage(String msg) {
        BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(10,10,480,480);
        g.setColor(Color.red);
        g.fillRect(245, 30, 10, 70);
        g.fillOval(243, 105, 12, 12);
        g.setColor(Color.black);
        g.drawRect(10,10,480,480);
        g.drawString("Fehler bei der Plugin-Graphik", 20, 50);
        try {
            String lines[] = msg.split("\\n");
            for (int i=0;i<lines.length;i++)
                g.drawString(lines[i], 20, 80+20*i);
        } catch(Exception e2) {
            g.drawString("Fehler bei der Bilderstellung!", 20, 250);
        }
        g.dispose();
        return bi;
    }

    /**
     * Internes Ergebnis der Bilderzeugung mit Dateiname, Format und Fehlertext.
     */
    private static class PImage{
        /**
         * Erzeugtes Bild.
         */
        public BufferedImage image;
        /**
         * Dateiname des Bildes.
         */
        public String filename;
        /**
         * Dateierweiterung des Bildes.
         */
        public String extension;
        /**
         * Fehlermeldung der Bilderzeugung, leer bei Erfolg.
         */
        public String error="";
        /**
         * Bündelt das erzeugte Bild mit Dateiname, Format und Fehlertext.
         *
         * @param image Bilddaten
         * @param filename Dateiname des Bildes
         * @param extension Dateierweiterung ohne Punkt
         * @param error Fehlermeldung, leer bei fehlerfreiem Ergebnis
         */
        public PImage(BufferedImage image,String filename,String extension,String error) {
            this.image = image;
            this.filename = filename;
            this.extension = extension;
            this.error = error;
        }
    }

    /**
     * Erzeugt ein Plugin-Bild und bestimmt Dateiname, Format und Fehlerstatus.
     *
     * @param params Parameterstring des Plugins
     * @param q Frage, in die das Plugin eingebettet ist
     * @return internes Ergebnis der Bilderzeugung
     */
    private PImage calcImage(String params, PluginQuestionDto q) {
        String error="";
        String extension = "png";
        PluginImageResultDto pluginImageResultDto = new PluginImageResultDto();
        if (params.startsWith("\"") && params.endsWith("\"")) params = params.substring(1,params.length()-1);
        // Bestimmung des aktuellen Prüfstrings des Bildes aus Parametern, Pluginname, Pluginversion und Plugindefinition
        String description = getPluginImageDescription(params,q);
        String filename = CryptTools.generateFilename(description,extension);
        // Berechnung des Bildes und speichern mit dem imageService
        BufferedImage image=null;
        if (filename.length()>0) {
            try {
                String parseresult;
                // ohne Timer
                parseDrawParams(params, q, pluginImageResultDto);
                image = getAWTImage(params, q, pluginImageResultDto);
                if (!pluginImageResultDto.isOk())
                    error = pluginImageResultDto.getMessages().toString();
            } catch (Exception ignored) {
                error = "Datei für das "+this.getTyp()+"-Plugin "+this.getName()+" kann nicht erzeugt werden!";
            }
        } else error = "Datei für das "+this.getTyp()+"-Plugin "+this.getName()+" kann nicht erzeugt werden!";
        return new PImage(image,filename,extension,error);
    }

    /**
     * Erstellt Metadaten für ein Plugin-Bild.
     *
     * @param filename Dateiname des Bildes
     * @param url URL des Bildes
     * @return Bildinformationen mit Größe, URL und Gültigkeitszeitpunkt
     */
    private ImageInfoDto calcImageInfoDto(String filename, String url){
        ImageInfoDto imageInfoDto = new ImageInfoDto(
                getVersion(),
                getTyp(),
                filename,
                url,
                getWidth(),
                getHeight(),
                IMAGEUNIT.percent,
                getImageWidthProzent(),
                "",
                "Plugin-Bild",
                getTyp()+" "+getName(),
                (new Date()).getTime()+365L*24L*3600L*1000L
        );
        return imageInfoDto;
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     *
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild in einem ImageBase64Dto
     *
     */
    @Override
    public ImageBase64Dto getImageDto(String params, PluginQuestionDto q) {
        String base64image="";
        PImage pImage = calcImage(params, q);
        if (pImage.image!=null) {
            try {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(pImage.image, pImage.extension, os);
                base64image = Base64.getEncoder().encodeToString(os.toByteArray());
            } catch (final Exception ioe) {
                pImage.error = "cannot write image for " + getTyp() + "-Plugin " + getName();
            }
        }
        ImageBase64Dto pluginImageDto = new ImageBase64Dto(
                base64image,
                calcImageInfoDto(pImage.filename,""),
                pImage.error
        );
        return pluginImageDto;
    }

    /**
     * Erzeugt und speichert ein Plugin-Bild und liefert seine URL und Metadaten.
     *
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Bild-URL und Bildinformationen einschließlich möglicher Fehlermeldungen
     */
    @Override
    public ImageUrlDto getImageUrl(String params, PluginQuestionDto q) {
        String base64image="";
        PImage pImage = calcImage(params, q);
        String url="";
        if (pImage.image!=null) {
            try {
                BaseImageService imageService = PluginConfiguration.pluginImageService;
                pImage.error += imageService.saveImage(pImage.image,pImage.filename);
                url = imageService.getURL(pImage.filename);
            } catch (final Exception ioe) {
                pImage.error = "cannot write image for " + getTyp() + "-Plugin " + getName();
            }
        }
        ImageUrlDto imageUrlDto = new ImageUrlDto(
                url,
                calcImageInfoDto(pImage.filename,url),
                pImage.error
        );
        return imageUrlDto;
    }

    /**
     * sucht alle Treffer vom Regex "pattern" im String s und gibt das
     * Suchergebnis zurück
     * @param pattern Regexp
     * @param s       String
     * @return        gefundene Treffer
     */
    public static Iterable<MatchResult> findMatches( String pattern, CharSequence s ) {
        List<MatchResult> results = new ArrayList<MatchResult>();
        for ( Matcher m = Pattern.compile(pattern,Pattern.DOTALL|Pattern.MULTILINE).matcher(s); m.find(); )
            results.add( m.toMatchResult() );

        return results;
    }

    /**
     * Liefert ein java.awt.BufferedImage Bild mit den angegebenen Parametern
     * @param   params   Parameter für die Bilderzeugung
     * @param q          Frage wo das Plugin eingebettet ist
     * @param pluginImageResultDto  nimmt alle Fehlermeldungen auf die beim Rendern des Bildes entstehen
     * @return           Bild als java.awt.BufferedImage
     */
    public BufferedImage getAWTImage(String params, PluginQuestionDto q, PluginImageResultDto pluginImageResultDto) {
        // Bild erzeugen
        params = HTMLtool.XMLToString(params);
        for (MatchResult m: findMatches("\\s*\"(.*)\"", params))
            params = m.group(1);
        try {
            BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            paint(g, pluginImageResultDto);
            g.dispose();
            return bi;
        } catch (Exception ex) {
            String s;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            s = sw.toString();
            return errorMessageImage(s);
        }
    }

    /**
     * Methode welche das Bild für die Ausgabe erzeugt
     * @param g Graphikhandle
     * @param pluginImageResultDto Nimmt alle Fehlermeldungen auf, welche beim Rendern des Bildes entstehen
     */
    public abstract void paint(Graphics2D g, PluginImageResultDto pluginImageResultDto);

    /**
     * Gibt eine Fehlermeldung im Graphics-Objekt aus
     * @param g   Graphics-Handler
     * @param msg Meldung
     */
    public void paintFehler(Graphics g, String msg) {
        int f = height/20;
        int y = f+f/3;
        g.setFont(new Font(g.getFont().getFamily(),g.getFont().getStyle(),f));
        g.setColor(Color.red);
        g.drawString("Fehler:", f, y);
        String m[] = msg.split("\\n");
        for (String s : m) {
            y+=(f*3)/2;
            msg = s.trim();
            g.setColor(Color.BLUE);
            g.drawString(msg, f, y);
        }
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     *
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     */
    @Override
    public Vector<String[]> getImageTemplates() {
        return null;
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     *
     * @param vars Alle Variablen der Frage
     * @param cp   Berechnungsparameter
     * @param p    Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     */
    @Override
    public CalcErgebnisDto parserPlugin(VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto ... p) {
        return null;
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskommt wenn die Parameter die Einheiten wie in der Liste p haben
     *
     * @param p Einheiten der Parameter
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     *
     */
    @Override
    public String parserPluginEinheit(String... p) {
        return null;
    }

    /**
     * Methode definiert das Eingabeelement der Subquestion, die das
     * Plugin verwendet
     * @return {@code JAVASCRIPT} bei aktivierter JavaScript-Schnittstelle, andernfalls {@code TextArea}
     */
    @Override
    public InputElement getInputElement() {
        if (isJavaScript()) return InputElement.JAVASCRIPT;
        return InputElement.TextArea;
    }

    /**
     * Passt die Plugindefinition an die Eingabe aus dem JavaScript-Result an. zB: Interaktive Karte
     *
     * @param pluginDef	akt. Plugin-Definition
     * @param jsResult	Rückgabe von JavaScript
     * @return	aktualisierte Plugindefinition
     *
     */
    @Override
    public String updatePluginstringJavascript(String pluginDef, String jsResult) {
        return pluginDef;
    }

    /**
     * Liefert eine Liste von JavaScript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden!
     *
     * @return für das Plugin notwendige JS-Libraries
     */
    @Override
    public List<JavascriptLibrary> javascriptLibraries() {
        return new Vector<>();
    }

    /**
     * Liefert eine Liste von LOKALEN JavaScript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     *
     * @return für das Plugin notwendige JS-Libraries
     */
    @Override
    public List<JavascriptLibrary> javascriptLibrariesLocal() {
        Vector<JavascriptLibrary> ret = new Vector<>();
        if (this.getJavascriptLibs()!=null)
            for (String lib:this.getJavascriptLibs()) {
                ret.add(new JavascriptLibrary(lib, PluginResource.readResourceFileString(lib), true));
            }
        return ret;
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren JavaScript - Bearbeitung
     *
     * @param params       Plugin-Parameter
     * @param q            Question, in die das Plugin eingebettet ist
     * @param nr           Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return {@code null}, da diese Funktion in dieser Implementierung nicht bereitgestellt wird
     */
    @Override
    public PluginDto loadPluginDto(String params, PluginQuestionDto q, int nr) {
        return null;
    }

    /**
     * Rendert ein Plugin für den Fragedruck als LaTeX-Quelltext
     * @param pluginDto Das PluginDto welches am Webserver an das JavaScript des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return          leeres Render-DTO; die Basisklasse erzeugt keine Ausgabe
     */
    @Override
    public PluginRenderDto renderLatex(PluginDto pluginDto, String answer, String mode){return new PluginRenderDto(); }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param tex                 true für LaTeX-Code, false für html-Code
     * @param pluginDto           PluginDto welches für die JavaScript aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Variablen der Frage
     * @param answerDto           Musterlösung und Informationen für die Bewertung
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    leeres Render-DTO; die Basisklasse erzeugt keine Ausgabe
     */
    @Override
    public PluginRenderDto renderPluginResult(boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade) {
        return new PluginRenderDto();
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @return                   alle notwendigen Konfig
     */
    @Override
    public PluginConfigurationInfoDto configurationInfo(String configurationID) {
        PluginConfigurationInfoDto pluginConfigurationInfoDto = new PluginConfigurationInfoDto(
            configurationID,
            getConfigurationMode(),
            isUseQuestion(),
            isUseVars(),
            isUseCVars(),
            isUseMaximaVars(),
            isUseMVars(),
            isAddDataSet(),
            isCalcMaxima(),
            isExternUrl(),
            getConfigPluginJS(),
            getPluginServiceURL()
        );
        return pluginConfigurationInfoDto;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     */
    @Override
    public PluginConfigDto setConfigurationData(String configuration, PluginQuestionDto questionDto) {
        this.config = configuration;
        PluginConfigDto pluginConfigDto = new PluginConfigDto();
        return pluginConfigDto;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @return                aktueller Konfigurationsstring
     */
    @Override
    public String getConfiguration() {
        return this.config;
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Text der innerhalb der PI Tags gestanden ist
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter Text
     */
    @Override
    public String modifyAngabe(String text, PluginQuestionDto q) {
        return text;
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Angabetext der Frage
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter AngabeText
     */
    @Override
    public String modifyAngabeTextkomplett(String text, PluginQuestionDto q) {
        return text;
    }


}
