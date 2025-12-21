package at.letto.plugindemojava.config;

import at.letto.plugindemojava.dto.AdminInfoDto;
import at.letto.plugindemojava.dto.ServiceInfoDTO;
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
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Date;

@Configuration
public class PluginConfiguration {

    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

    public static final String PLUGIN_NAME     = "DemoJava";
    public static final String PLUGIN_VERSION  = "1.0";
    public static final String PLUGIN_AUTHOR   = "LeTTo GmbH";
    public static final String PLUGIN_LICENSE  = "OpenSource";

    @Value("${letto.setup.uri:http://localhost:8096}")
    @Getter
    private String setupServiceUri;

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

    public void init() {
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
