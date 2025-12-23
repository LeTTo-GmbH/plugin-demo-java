package at.letto.plugindemojava.config;

import at.letto.plugindemojava.dto.AdminInfoDto;
import at.letto.plugindemojava.dto.ConfigServiceDto;
import at.letto.plugindemojava.dto.RegisterServiceResultDto;
import at.letto.plugindemojava.dto.ServiceInfoDTO;
import at.letto.plugindemojava.plugin.PluginService;
import at.letto.plugindemojava.service.PluginConnectionServiceInterface;
import at.letto.plugindemojava.tools.Datum;
import at.letto.plugindemojava.tools.ServerStatus;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;

@Configuration
public class PluginConfiguration {

    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

    public static final String PLUGIN_NAME     = "letto-plugindemojava";
    public static final String PLUGIN_VERSION  = "1.0";
    public static final String PLUGIN_AUTHOR   = "LeTTo GmbH";
    public static final String PLUGIN_LICENSE  = "OpenSource";

    /** address of plugin in docker-network nw-letto */
    @Value("${network.letto.address:letto-plugindemojava}")
    @Getter  private String networkLettoAddress;

    /** name of the docker container */
    @Value("${docker.container.name:letto-plugindemojava}")
    @Getter  private String dockerName;

    @Value("${letto.setup.uri:http://localhost:8096}")
    @Getter private String setupServiceUri;

    @Value("${letto.user.user.name:user}")
    @Getter  private String userUserName;

    @Value("${letto.user.user.password:}")
    @Getter private String userUserPassword;

    @Value("${letto.plugin.uri.intern:}")
    @Getter private String uriIntern;

    @Value("${letto.plugin.uri.extern:}")
    @Getter private String baseUriExtern;

    @Value("${servername:}")
    @Getter private String servername;

    /** externe URI welche an das Setup-service übergeben wird */
    @Getter private String uriExtern;

    /** externe URI für das PluginDto */
    @Getter private String uriGetPluginDto;

    @Getter private WebClient webClientSetupUser;

    @Autowired
    private WebClient.Builder   webClientBuilder;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebServerApplicationContext webServerAppCtxt;

    @Autowired
    private ServerStatus serverStatus;

    private AdminInfoDto   adminInfoDto = null;
    private ServiceInfoDTO serviceInfoDTO = null;

    /** Alle registrierten Plugins in einer Hashmap mit Pluginname und PluginConnectionService */
    public HashMap<String, PluginService> plugins = new HashMap<>();

    /** statische Referenz auf die PluginConfiguration */
    public static PluginConfiguration pluginConfiguration=null;

    public void init() {
        pluginConfiguration = this;
        // Interne Uri welche ans Setup weitergegeben wird bestimmen
        uriIntern = System.getenv("letto.plugin.uri.intern");
        while (uriIntern!=null && uriIntern.endsWith("/")) uriIntern=uriIntern.substring(0,uriIntern.length()-1);
        if (uriIntern==null || uriIntern.trim().length()==0)
            uriIntern = "http://letto-pluginuhr.nw-letto:8080";
        uriIntern = uriIntern + Endpoint.LOCAL_API;

        // externe Uri welche ans Setup weitergegeben wird bestimmen
        baseUriExtern = System.getenv("letto.plugin.uri.extern");
        while (baseUriExtern!=null && baseUriExtern.endsWith("/")) baseUriExtern=baseUriExtern.substring(0,baseUriExtern.length()-1);
        if (baseUriExtern==null || baseUriExtern.trim().length()==0)
            baseUriExtern = "https://"+servername;
        uriExtern       = baseUriExtern + Endpoint.EXTERN_API;
        uriGetPluginDto = baseUriExtern + Endpoint.EXTERN_OPEN+Endpoint.reloadPluginDto;

        // WebClient zum Setup-Service
        webClientSetupUser = webClientBuilder
                .baseUrl(setupServiceUri)
                .defaultHeaders(h -> h.setBasicAuth(userUserName, userUserPassword))
                .build();
    }

    public void insertPlugin(PluginService pluginService) {
        plugins.put(pluginService.getName(),pluginService);
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
            result = getWebClientSetupUser()
                    .post()
                    .uri("/config/auth/user/registerplugin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(configServiceDto)
                    .retrieve()
                    .onStatus(
                            // Predicate: use a lambda that calls isError()
                            status -> status.isError(),
                            // Fehler-Handler: lies den Body als String (oder DTO) und erzeuge ein Mono<Throwable>
                            resp -> resp.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException("Remote error: " + body)))
                    )
                    // Erfolgsfall: parse als DTO
                    .bodyToMono(RegisterServiceResultDto.class)
                    .block();
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
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

    public AdminInfoDto getAdminInfoDto() {
        if (adminInfoDto == null) adminInfoDto = calcAdminInfo();
        return adminInfoDto;
    }

    public ServiceInfoDTO getServiceInfoDto() {
        if (serviceInfoDTO==null) serviceInfoDTO = calcServiceInfoDTO();
        return serviceInfoDTO;
    }

    private ServiceInfoDTO calcServiceInfoDTO() {
        AdminInfoDto adminInfoDto = getAdminInfoDto();
        ServiceInfoDTO serviceInfoDTO = new ServiceInfoDTO(
                applicationContext.getId(), // Name des Services
                PLUGIN_VERSION, // Version des Services
                PLUGIN_AUTHOR,               // Information über den Autor des Services
                PLUGIN_LICENSE ,          // Information über die Lizenz des Services
                "",                         // Information über die Endpoints des Services
                serverStatus.getJarFileName(),
                Datum.formatDateTime(new Date(applicationContext.getStartupDate())),
                adminInfoDto,
                null
        );
        return serviceInfoDTO;
    }

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


}
