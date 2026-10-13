package at.letto.plugins.config;

import at.letto.plugins.dto.*;
import at.letto.plugins.plugin.PluginService;
import at.letto.plugins.tools.BaseImageService;
import at.letto.plugins.tools.Datum;
import at.letto.plugins.tools.ServerStatus;
import lombok.Getter;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.boot.web.server.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verwaltet Plugin-Registrierung, Service-Informationen, Bildablage und Konfigurationsverbindungen.
 */
@Configuration
public class PluginConfiguration {

    /**
     * Logger für Meldungen dieser Klasse.
     */
    private Logger logger = LoggerFactory.getLogger(PluginConfiguration.class);

    /**
     * Name des Plugin-Services.
     */
    public static final String PLUGIN_NAME     = "letto-plugindemo";
    /**
     * Version des Plugin-Services.
     */
    public static final String PLUGIN_VERSION  = "1.0";
    /**
     * Autor des Plugin-Services.
     */
    public static final String PLUGIN_AUTHOR   = "LeTTo GmbH";
    /**
     * Lizenzangabe des Plugin-Services.
     */
    public static final String PLUGIN_LICENSE  = "OpenSource";

    /** address of plugin in docker-network nw-letto */
    @Value("${network.letto.address:letto-plugindemo}")
    @Getter  private String networkLettoAddress;

    /** name of the docker container */
    @Value("${docker.container.name:letto-plugindemo}")
    @Getter  private String dockerName;

    /**
     * Basis-URI des Setup-Services.
     */
    @Value("${letto.setup.uri:http://localhost:8096}")
    @Getter private String setupServiceUri;

    /**
     * Benutzername für die Anmeldung am Setup-Service.
     */
    @Value("${letto.user.user.name:user}")
    @Getter  private String userUserName;

    /**
     * Passwort für die Anmeldung am Setup-Service.
     */
    @Value("${letto.user.user.password:}")
    @Getter private String userUserPassword;

    /**
     * Interne URI des Plugin-Services.
     */
    @Value("${letto.plugin.uri.intern:}")
    @Getter private String uriIntern;

    /**
     * Externe Basis-URI des Plugin-Services.
     */
    @Value("${letto.plugin.uri.extern:}")
    @Getter private String baseUriExtern;

    /**
     * Konfigurierter Servername.
     */
    @Value("${servername:}")
    @Getter private String servername;

    /**
     * Lokales Verzeichnis für Plugin-Bilder.
     */
    @Value("${letto.pathImages}")
    @Getter private String pathImages;

    /**
     * Webpfad für Plugin-Bilder.
     */
    @Value("${letto.webPathImages}")
    @Getter private String webpathImages;

    /**
     * Lokales Verzeichnis für Plugin-Ressourcen.
     */
    @Value("${letto.pathPlugins}")
    @Getter private String pathPlugins;

    /**
     * Webpfad für Plugin-Ressourcen.
     */
    @Value("${letto.webPathPlugins}")
    @Getter private String webpathPlugins;

    /** externe URI welche an das Setup-service übergeben wird */
    @Getter private String uriExtern;

    /** externe URI für das PluginDto */
    @Getter private String uriGetPluginDto;

    /**
     * HTTP-Client für authentifizierte Anfragen an den Setup-Service.
     */
    @Getter private RestClient restClientSetupUser;

    /**
     * Builder zum Erzeugen von HTTP-Clients.
     */
    @Autowired
    private RestClient.Builder  restClientBuilder;

    /**
     * Spring-Anwendungskontext.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Anwendungskontext des Servlet-Webservers.
     */
    @Autowired
    private WebServerApplicationContext webServerAppCtxt;

    /**
     * Dienst zum Ermitteln von Laufzeitinformationen.
     */
    @Autowired
    private ServerStatus serverStatus;

    /**
     * Beim ersten Zugriff zwischengespeicherte Administrationsinformationen.
     */
    private AdminInfoDto   adminInfoDto = null;
    /**
     * Beim ersten Zugriff zwischengespeicherte Service-Informationen.
     */
    private ServiceInfoDTO serviceInfoDTO = null;

    /** Plugin-Information über alle registrierten Plugins des Services */
    private final Map<String, PluginGeneralInfo> plugins = new ConcurrentHashMap<>();

    /**
     * Konfigurationsverbindungen nach ihrer eindeutigen Sitzungs-ID.
     */
    private final ConcurrentHashMap<String,PluginConfigurationConnection> configurations = new ConcurrentHashMap<>();

    /** Bildablage für die Plugin-Bilder, wird jedem erzeugten Plugin übergeben */
    @Getter private BaseImageService pluginImageService;

    /**
     * Gemeinsame Bildablage des Plugin-Services.
     */
    @Getter private BaseImageService serverImageService;

    /**
     * Initialisiert den Setup-HTTP-Client und die Bildablage des Plugin-Services.
     */
    public void init() {
        // Interne Uri welche ans Setup weitergegeben wird bestimmen
        uriIntern = System.getenv("letto.plugin.uri.intern");
        while (uriIntern!=null && uriIntern.endsWith("/")) uriIntern=uriIntern.substring(0,uriIntern.length()-1);
        if (uriIntern==null || uriIntern.trim().length()==0)
            uriIntern = "http://letto-plugindemo.nw-letto:8080";
        uriIntern = uriIntern + Endpoint.LOCAL_API;

        // externe Uri welche ans Setup weitergegeben wird bestimmen
        baseUriExtern = System.getenv("letto.plugin.uri.extern");
        while (baseUriExtern!=null && baseUriExtern.endsWith("/")) baseUriExtern=baseUriExtern.substring(0,baseUriExtern.length()-1);
        if (baseUriExtern==null || baseUriExtern.trim().length()==0)
            baseUriExtern = "https://"+servername;
        uriExtern       = baseUriExtern + Endpoint.EXTERN_API;
        uriGetPluginDto = baseUriExtern + Endpoint.EXTERN_OPEN+Endpoint.reloadPluginDto;

        // RestClient zum Setup-Service
        // HTTP/1.1 ohne h2c-Upgrade und gepufferter Body mit Content-Length statt chunked, wie zuvor beim WebClient
        restClientSetupUser = restClientBuilder
                .requestFactory(new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build())))
                .baseUrl(setupServiceUri)
                .defaultHeaders(h -> h.setBasicAuth(userUserName, userUserPassword))
                .build();

        // Image-Service für die Plugin-Bilder
        pluginImageService = new BaseImageService(pathPlugins+Endpoint.servicepath, webpathPlugins+Endpoint.servicepath, true);
        logger.info("URL Plugin-Image-Service: "+pluginImageService.getUrlPath());
        serverImageService = new BaseImageService(pathImages, webpathImages, true);
        logger.info("URL Server-Image-Service: "+serverImageService.getUrlPath());
    }

    /** registriert das Plugin am Setup-Service */
    public void registerPluginInSetup() {
        //RestSetupService setupService = lettoService.getSetupService();
        HashMap<String,String> params = new HashMap<>();

        ConfigServiceDto configServiceDto = new ConfigServiceDto(
                PLUGIN_NAME,
                PLUGIN_VERSION,
                PLUGIN_AUTHOR,
                PLUGIN_LICENSE,
                serverStatus.getBetriebssystem(),
                serverStatus.getIP(),
                serverStatus.getEncoding(),
                serverStatus.getJavaVersion(),
                networkLettoAddress,
                dockerName,
                getUriIntern(),
                true,
                getUriExtern(),
                true,
                false,
                true,
                getUserUserName(),
                getUserUserPassword(),
                false,
                Datum.toDateInteger(new Date(applicationContext.getStartupDate())),
                Datum.nowDateInteger(),
                params
        );
        String setupUri = getSetupServiceUri();
        //hier kommt die REST-Anfrage an den Server
        RegisterServiceResultDto result=null;
        try {
            result = getRestClientSetupUser()
                    .post()
                    .uri("/config/auth/user/registerplugin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(configServiceDto)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            // Fehler-Handler: Body der Fehlerantwort in die Exception übernehmen
                            (request, resp) -> {
                                String body = new String(resp.getBody().readAllBytes(), StandardCharsets.UTF_8);
                                throw new RuntimeException("Remote error: " + body);
                            }
                    )
                    // Erfolgsfall: parse als DTO
                    .body(RegisterServiceResultDto.class);
        } catch (RuntimeException e) {
            // bei Verbindungsfehlern steht die eigentliche Ursache in getCause()
            logger.error(e.getMessage()+(e.getCause()!=null ? " - "+e.getCause() : ""));
        }

        if (result==null) {
            logger.error("setup service cannot be reached at "+setupUri);
        } else if (!result.isRegistrationOK()) {
            logger.error("setup service cannot register this plugin! -> "+result.getMsg());
        } else {
            int count = result.getRegistrationCounter();
            boolean isnew = result.isNewRegistered();
            logger.info("Plugin registered in setup-Service "+
                    (isnew?"NEW":"UPDATED")+" "+
                    (count>1?", "+count+" instances":""));
        }
    }

    /**
     * Liefert die beim ersten Zugriff ermittelten Administrationsinformationen.
     *
     * @return zwischengespeicherte Administrationsinformationen
     */
    public AdminInfoDto getAdminInfoDto() {
        if (adminInfoDto == null) adminInfoDto = calcAdminInfo();
        return adminInfoDto;
    }

    /**
     * Liefert die beim ersten Zugriff ermittelten Service-Informationen.
     *
     * @return zwischengespeicherte Service-Informationen
     */
    public ServiceInfoDTO getServiceInfoDto() {
        if (serviceInfoDTO==null) serviceInfoDTO = calcServiceInfoDTO();
        return serviceInfoDTO;
    }

    /**
     * Erstellt Service-Informationen aus Anwendungskontext und Administrationsdaten.
     *
     * @return Service-Informationen
     */
    private ServiceInfoDTO calcServiceInfoDTO() {
        AdminInfoDto adminInfoDto = getAdminInfoDto();
        ServiceInfoDTO serviceInfoDTO = new ServiceInfoDTO(
                applicationContext.getId(), // Name des Services
                PLUGIN_VERSION, // Version des Services
                PLUGIN_AUTHOR,               // Information über den Autor des Services
                PLUGIN_LICENSE ,             // Information über die Lizenz des Services
                "",                          // Information über die Endpoints des Services
                serverStatus.getJarFileName(),
                Datum.formatDateTime(new Date(applicationContext.getStartupDate())),
                adminInfoDto,
                null
        );
        return serviceInfoDTO;
    }

    /**
     * Ermittelt Administrationsdaten zur aktuellen Laufzeitumgebung.
     *
     * @return Administrationsdaten
     */
    private AdminInfoDto calcAdminInfo() {
        String pid = new ApplicationPid().toString();
        String applicationname = applicationContext.getId();
        String applicationhome = new ApplicationHome().toString();
        long uptime = System.currentTimeMillis()-applicationContext.getStartupDate();
        String serverversion = "";
        String springBootCoreVersion = SpringVersion.getVersion();
        String springBootStarterVersion = "";
        try {
                springBootStarterVersion = ServerStatus.mainManifest.getMainAttributes().getValue("Spring-Boot-Version");

                ApplicationContext context = ServerStatus.mainContext;
                if (springBootStarterVersion==null)
                    springBootStarterVersion = ((AnnotationConfigServletWebServerApplicationContext) context).getWebServer().getClass().getPackage().getImplementationVersion();
                if (springBootStarterVersion==null) springBootStarterVersion="";

                TomcatWebServer tomcatWebServer = (TomcatWebServer)((AnnotationConfigServletWebServerApplicationContext) context).getWebServer();
                Tomcat tomcat = tomcatWebServer.getTomcat();
                String state  = tomcat.getServer().getStateName();
                String servername = tomcat.getService().getName();

                String implementationVersion = serverStatus.getTomeeRevision();
                serverversion = servername+" "+implementationVersion;
        } catch (Exception ex) {}
        serverversion = "Spring-Boot Core:"+ springBootCoreVersion+", Starter: "+springBootStarterVersion+(serverversion!=null && serverversion.length()>0?", "+serverversion:"");
        AdminInfoDto adminInfoDto = new AdminInfoDto(
                applicationname,
                pid,
                applicationhome,
                applicationContext.getStartupDate(),
                uptime,
                PLUGIN_VERSION,
                Datum.formatDateTime(Datum.nowLocalDateTime()), // (new Date()).toString(),
                serverStatus.getBetriebssystem(),
                serverStatus.getIPs(),
                serverStatus.getEncoding(),
                serverStatus.getFileEncoding(),
                serverStatus.getFileSeparator(),
                serverStatus.getJavaSpecificationVersion(),
                serverStatus.getJavaVendor(),
                serverStatus.getJavaVersion(),
                serverStatus.getJavaVersionNumber(),
                serverStatus.getHostname(),
                serverStatus.getLanguage(),
                serverStatus.getLinuxDescription(),
                serverStatus.getLinuxDistribution(),
                serverStatus.getLinuxRelease(),
                serverStatus.getServerUsername(),
                serverversion,
                serverStatus.getSystemHome(),
                serverStatus.isLinux(),
                serverStatus.isUbuntu(),
                serverStatus.isWindows(),
                webServerAppCtxt.getWebServer().getPort(),0,0
        );
        return adminInfoDto;
    }

    /**
     * Registriert einen Plugin-Typ im Service.
     * @param typ         Name des Plugins
     * @param classname   Klassenpfad des Plugins
     */
    protected void registerPlugin(String typ, String classname) {
        try {
            Class<?> c = Class.forName(classname);
            Constructor<?> constr = c.getConstructor(String.class, String.class);
            PluginService pi = (PluginService) constr.newInstance("", "");
            PluginGeneralInfo info = pi.getPluginGeneralInfo();
            info.setTyp(typ);
            info.setPluginType(classname);
            plugins.put(typ, info);
            logger.info("Plugin registriert: "+typ+" -> "+classname);
        } catch (NoClassDefFoundError | Exception e) {
            logger.error("Plugin "+typ+" ("+classname+") kann nicht registriert werden", e);
        }
    }

    /**
     * Liefert die Namen aller registrierten Plugin-Typen.
     *
     * @return Liste der Plugin-Typen
     */
    public List<String> getPluginList() {
        List<String> pluginList = new ArrayList<>();
        for (String typ:plugins.keySet()) pluginList.add(typ);
        return pluginList;
    }

    /**
     * Liefert die globalen Informationen aller registrierten Plugins.
     *
     * @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services
     */
    public List<PluginGeneralInfo> getPluginGeneralInfoList() {
        List<PluginGeneralInfo> pluginList = new ArrayList<>();
        for (String typ:plugins.keySet()) pluginList.add(plugins.get(typ));
        return pluginList;
    }

    /**
     * Liefert die allgemeinen Informationen eines registrierten Plugin-Typs.
     *
     * @param typ Plugin Typ
     * @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     *
     */
    public PluginGeneralInfo getPluginGeneralInfo(String typ) {
        // ConcurrentHashMap erlaubt keine null-Schlüssel
        if (typ == null) return null;
        return plugins.get(typ);
    }

    /**
     * Erzeugt aus dem Plugin-Typ und den Parametern ein PluginService Objekt<br>
     * Sollte nur intern im Spring Boot-Service verwendet werden!<br>
     * Bis der Plugin-Config-Dialog umgestellt ist wird die Funktion auch von
     * JSF für den Konfigurationsdialog verwendet, sollte später aber auf
     * private gesetzt werden!!
     * @param typ     Typ des Plugins
     * @param name    Namen des Plugins in  der Frage
     * @param params  Parameterstring des Plugins für die Konfiguration
     * @return        erzeugtes Plugin
     */
    public PluginService createPluginService(String typ, String name, String params) {
        PluginGeneralInfo info = getPluginGeneralInfo(typ);
        if (info != null) {
            try {
                Class<?> c = Class.forName(info.getPluginType());
                Constructor<?> constr = c.getConstructor(String.class, String.class);
                PluginService pi = (PluginService) constr.newInstance(name, params);
                pi.setImageService(pluginImageService);
                return pi;
            } catch (Exception ex) {
                // Fehler im Konstruktor des Plugins kommen als InvocationTargetException und stehen in getCause()
                Throwable cause = ex instanceof InvocationTargetException && ex.getCause()!=null ? ex.getCause() : ex;
                logger.error("Plugin "+typ+" kann nicht erzeugt werden", cause);
            }
        }
        return null;
    }

    /* -----------------------------------------------------------------------------------------------------------------------------------------------------
     *                   Konfigurationsdialog
     * -----------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param typ                Typ des Plugins
     * @param name               Name des Plugins in der Frage
     * @param config             Konfigurationsstring des Plugins
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @param timeout            maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen
     * @return                   alle notwendigen Konfig
     */
    public PluginConfigurationInfoDto configurationInfo(String typ, String name, String config, String configurationID, long timeout){
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) {
            PluginConfigurationConnection connection = addConnection(
                    typ, name, config, configurationID, pluginService, timeout
            );
            return connection.getPluginConfigurationInfoDto();
        }
        PluginConfigurationInfoDto result = new PluginConfigurationInfoDto();
        return result;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     *
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     *
     * @param typ Typ des Plugins
     */
    public PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto) {
        PluginConfigurationConnection connection = getConnection(configurationID);
        if (connection!=null && connection.getPluginService()!=null) {
            PluginService pluginService = connection.getPluginService();
            connection.pluginConfigDto = pluginService.setConfigurationData(configuration,questionDto);
            connection.pluginConfigDto.setConfigurationID(configurationID);
            connection.pluginConfigDto.setTyp(typ);
            connection.pluginConfigDto.setName(connection.getName());
            connection.pluginConfigDto.setConfig(configuration);
            connection.pluginQuestionDto = questionDto;
            connection.pluginConfigDto.setPluginDto(
                    createPluginService(typ,connection.getName(),connection.getConfig())
                    .loadPluginDto("",questionDto,0));
            connection.pluginConfigDto.setTagName(connection.getName());
            return connection.pluginConfigDto;
        }
        PluginConfigDto pluginConfigDto = new PluginConfigDto();
        pluginConfigDto.setConfigurationID(configurationID);
        pluginConfigDto.setTagName(typ);
        pluginConfigDto.setErrorMsg("cannot find connection or plugin");
        return pluginConfigDto;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     *
     * @param configurationID zu verwendende Konfigurations-ID
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     *
     * @param typ Typ des Plugins
     */
    public String getConfiguration(String typ, String configurationID) {
        PluginConfigurationConnection connection = getConnection(configurationID);
        if (connection!=null && connection.getPluginService()!=null) {
            PluginService pluginService = connection.getPluginService();
            try {
                return pluginService.getConfiguration();
            } catch (Exception ex) {
                return "@Error: error during configuration of plugin";
            }
        }
        return "@Error:cannot find connection or plugin";
    }

    /**
     * Sucht eine gültige Verbindung mit einer vorhandenen Plugin-Instanz.
     *
     * @param typ Typ des Plugins
     * @param configurationID eindeutige ID der Konfigurationssitzung
     * @return Konfigurationsverbindung oder {@code null}, wenn keine passende Verbindung existiert
     */
    public PluginConfigurationConnection getConfigurationConnection(String typ, String configurationID) {
        PluginConfigurationConnection connection = getConnection(configurationID);
        if (connection!=null && connection.getPluginService()!=null) {
            return connection;
        }
        return null;
    }

    /**
     * Erstellt oder aktualisiert eine Konfigurationsverbindung und entfernt abgelaufene Sitzungen.
     *
     * @param typ Typ des Plugins
     * @param name Name beziehungsweise Bezeichner
     * @param config Konfigurationsstring des Plugins
     * @param configurationID eindeutige ID der Konfigurationssitzung
     * @param pluginService zugehörige Plugin-Instanz
     * @param timeout maximale Inaktivitätsdauer der Sitzung in Sekunden
     * @return erstellte oder aktualisierte Verbindung
     */
    private PluginConfigurationConnection addConnection(String typ, String name, String config, String configurationID, PluginService pluginService, long timeout) {
        PluginConfigurationConnection connection;
        if (configurationID!=null && configurations.containsKey(configurationID)) {
            connection = configurations.get(configurationID);
            connection.changeConfig(config, pluginService);
        } else connection = new PluginConfigurationConnection(
                typ,name,config,configurationID,pluginService,timeout
        );
        configurations.put(connection.configurationID,connection);
        removeOutdatedConnections();
        return connection;
    }

    /**
     * Entfernt Konfigurationsverbindungen, deren Inaktivitätsfrist abgelaufen ist.
     */
    private void removeOutdatedConnections() {
        try {
            long now = Datum.nowDateInteger();
            Set<String> keys = configurations.keySet();
            for (String key:keys) try {
                PluginConfigurationConnection c = configurations.get(key);
                if (now-c.lastTime>c.timeout) {
                    // connection outdated
                    configurations.remove(key);
                }
            } catch (Exception ex) {}
        } catch (Throwable t) {
            System.out.println("error during remove outdated plugin-connections!");
            t.printStackTrace();
        }
    }

    /**
     * Sucht eine Konfigurationsverbindung und aktualisiert deren letzten Zugriffszeitpunkt.
     *
     * @param configurationID eindeutige ID der Konfigurationssitzung
     * @return Verbindung oder {@code null}, wenn die Sitzung fehlt oder abgelaufen ist
     */
    private PluginConfigurationConnection getConnection(String configurationID) {
        removeOutdatedConnections();
        if (configurationID!=null && configurationID.trim().length()>0 && configurations.containsKey(configurationID)) {
            PluginConfigurationConnection connection = configurations.get(configurationID);
            connection.lastTime = Datum.nowDateInteger();
            return connection;
        }
        return null;
    }



}
