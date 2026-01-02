package at.letto.plugin.config;


public class Endpoint {

    //-------------------------- Endpoint-Verzeichnisse

    public static final String servicepath  = "/plugindemojava";
    public static final String open         = servicepath+"/open";
    public static final String LOCAL_API    = "/open";
    public static final String EXTERN_API   = servicepath+"/auth/user";
    public static final String EXTERN_OPEN  = servicepath+"/api/open";

    //-------------------------- Info Endpoints
    public static final String ping         = "/ping";
    public static final String pingopen     = open+"/ping";
    public static final String INFO             = "/info";
    public static final String INFO_OPEN        = open+INFO;
    public static final String VERSION          = "/version";

    //-------------------------- Allgemeine Endpoints für alle Plugins -----------------

    public static final String getPluginList               = "/pluginlist";
    public static final String getPluginGeneralInfoList    = "/generalinfolist";
    public static final String getPluginGeneralInfo        = "/generalinfo";
    public static final String getHTML                     = "/gethtml";
    public static final String getAngabe                   = "/angabe";
    public static final String generateDatasets            = "/generatedatasets";
    public static final String getMaxima                   = "/maxima";
    public static final String getImage                    = "/image";
    public static final String getImageUrl                 = "/imageurl";
    public static final String getImageTemplates           = "/imagetemplates";
    public static final String parserPlugin                = "/parserplugin";
    public static final String parserPluginEinheit         = "/parserplugineinheit";
    public static final String score                       = "/score";
    public static final String getVars                     = "/getvars";
    public static final String modifyAngabe                = "/modifyangabe";
    public static final String modifyAngabeTextkomplett    = "/modifyangabetextkomplett";
    public static final String updatePluginstringJavascript= "/updatepluginstringjavascript";
    public static final String loadPluginDto               = "/loadplugindto";
    public static final String reloadPluginDto             = "/reloadplugindto";
    public static final String renderLatex                 = "/renderlatex";
    public static final String renderPluginResult          = "/renderpluginresult";
    public static final String configurationInfo           = "/configurationinfo";
    public static final String configurationHttp           = "/confighttp";
    public static final String setConfigurationData        = "/setconfigurationdata";
    public static final String getConfiguration            = "/getconfiguration";

    public static final String iframeConfig = open + configurationHttp;

    public String getIframeConfig() {
        return iframeConfig;
    }

    public String getLogoEP10() {
        return open+"/images/letto2-10.png";
    }

}
