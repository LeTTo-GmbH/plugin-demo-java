package at.letto.plugins.config;

/**
 * Zentrale Pfade der REST-Endpunkte und öffentlichen Ressourcen des Plugin-Services.
 */
public class Endpoint {

    //-------------------------- Endpoint-Verzeichnisse

    /**
     * Basis des öffentlichen Servicepfads.
     */
    public static final String servicepath  = "/plugindemo";
    /**
     * Basis des öffentlichen Ressourcenpfads.
     */
    public static final String open         = servicepath+"/open";
    /**
     * Basis der internen Plugin-API.
     */
    public static final String LOCAL_API    = "/open";
    /**
     * Basis der authentifizierten externen API.<br>
     * Wird bei der Registrierung als externe URI ({@code uriExtern}) an das Setup-Service gemeldet.
     * Ein Controller für eine authentifizierte externe API ist in diesem Demo nicht implementiert.
     */
    public static final String EXTERN_API   = servicepath+"/auth/user";
    /**
     * Basis der öffentlichen externen Plugin-API.
     */
    public static final String EXTERN_OPEN  = servicepath+"/api/open";

    //-------------------------- Info Endpoints
    /**
     * Pfad zur internen Erreichbarkeitsprüfung.
     */
    public static final String ping             = "/ping";
    /**
     * Pfad zur öffentlichen Erreichbarkeitsprüfung.
     */
    public static final String pingopen         = open+"/ping";
    /**
     * Pfad für Service-Informationen.
     */
    public static final String INFO             = "/info";
    /**
     * Öffentlicher Pfad für Service-Informationen.
     */
    public static final String INFO_OPEN        = open+INFO;
    /**
     * Pfad zur Abfrage der Service-Version.
     */
    public static final String VERSION          = "/version";

    //-------------------------- Allgemeine Endpoints für alle Plugins -----------------

    /**
     * Relativer API-Pfad für die Operation {@code getPluginList}.
     */
    public static final String getPluginList               = "/pluginlist";
    /**
     * Relativer API-Pfad für die Operation {@code getPluginGeneralInfoList}.
     */
    public static final String getPluginGeneralInfoList    = "/generalinfolist";
    /**
     * Relativer API-Pfad für die Operation {@code getPluginGeneralInfo}.
     */
    public static final String getPluginGeneralInfo        = "/generalinfo";
    /**
     * Relativer API-Pfad für die Operation {@code getHTML}.
     */
    public static final String getHTML                     = "/gethtml";
    /**
     * Relativer API-Pfad für die Operation {@code getAngabe}.
     */
    public static final String getAngabe                   = "/angabe";
    /**
     * Relativer API-Pfad für die Operation {@code generateDatasets}.
     */
    public static final String generateDatasets            = "/generatedatasets";
    /**
     * Relativer API-Pfad für die Operation {@code getMaxima}.
     */
    public static final String getMaxima                   = "/maxima";
    /**
     * Relativer API-Pfad für die Operation {@code getImage}.
     */
    public static final String getImage                    = "/image";
    /**
     * Relativer API-Pfad für die Operation {@code getImageUrl}.
     */
    public static final String getImageUrl                 = "/imageurl";
    /**
     * Relativer API-Pfad für die Operation {@code getImageTemplates}.
     */
    public static final String getImageTemplates           = "/imagetemplates";
    /**
     * Relativer API-Pfad für die Operation {@code parserPlugin}.
     */
    public static final String parserPlugin                = "/parserplugin";
    /**
     * Relativer API-Pfad für die Operation {@code parserPluginEinheit}.
     */
    public static final String parserPluginEinheit         = "/parserplugineinheit";
    /**
     * Relativer API-Pfad für die Operation {@code score}.
     */
    public static final String score                       = "/score";
    /**
     * Relativer API-Pfad für die Operation {@code getVars}.
     */
    public static final String getVars                     = "/getvars";
    /**
     * Relativer API-Pfad für die Operation {@code modifyAngabe}.
     */
    public static final String modifyAngabe                = "/modifyangabe";
    /**
     * Relativer API-Pfad für die Operation {@code modifyAngabeTextkomplett}.
     */
    public static final String modifyAngabeTextkomplett    = "/modifyangabetextkomplett";
    /**
     * Relativer API-Pfad für die Operation {@code updatePluginstringJavaScript}.
     */
    public static final String updatePluginstringJavascript= "/updatepluginstringjavascript";
    /**
     * Relativer API-Pfad für die Operation {@code loadPluginDto}.
     */
    public static final String loadPluginDto               = "/loadplugindto";
    /**
     * Relativer API-Pfad für die Operation {@code reloadPluginDto}.
     */
    public static final String reloadPluginDto             = "/reloadplugindto";
    /**
     * Relativer API-Pfad für die Operation {@code renderLatex}.
     */
    public static final String renderLatex                 = "/renderlatex";
    /**
     * Relativer API-Pfad für die Operation {@code renderPluginResult}.
     */
    public static final String renderPluginResult          = "/renderpluginresult";
    /**
     * Relativer API-Pfad für die Operation {@code configurationInfo}.
     */
    public static final String configurationInfo           = "/configurationinfo";
    /**
     * Relativer API-Pfad für die Operation {@code configurationHttp}.
     */
    public static final String configurationHttp           = "/confighttp";
    /**
     * Relativer API-Pfad für die Operation {@code setConfigurationData}.
     */
    public static final String setConfigurationData        = "/setconfigurationdata";
    /**
     * Relativer API-Pfad für die Operation {@code getConfiguration}.
     */
    public static final String getConfiguration            = "/getconfiguration";

    /**
     * Öffentlicher Pfad des eingebetteten Konfigurationsdialogs.
     */
    public static final String iframeConfig = open + configurationHttp;

    /**
     * Liefert den Pfad des eingebetteten Konfigurationsdialogs.
     *
     * @return Pfad des Konfigurationsdialogs
     */
    public String getIframeConfig() {
        return iframeConfig;
    }

    /**
     * Liefert den öffentlichen Pfad des LeTTo-Logos.
     *
     * @return Pfad der Logo-Datei
     */
    public String getLogoEP10() {
        return open+"/images/letto2-10.png";
    }

}















