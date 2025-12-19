package at.letto.plugindemojava.config;

import at.letto.plugindemojava.tools.IP;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

@Configuration
public class PluginConfiguration {

    private Logger logger = LoggerFactory.getLogger(StartupConfiguration.class);

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

    /** @return Liefert den Distributionsnamen der Linux Distribution wenn es sich um eine Linux-System handelt */
    public static String getLinuxDistribution() {
        if (isLinux()) try {
            List<String> v = Files.readAllLines(Path.of("/etc/lsb-release"));
            for (String s:v) {
                String t[] = s.trim().split("=");
                if (t[0].trim().equals("DISTRIB_ID")) {
                    s = t[1].trim();
                    if (s.startsWith("\"")) s=s.substring(1);
                    if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
                    return s;
                }
            }
        } catch (Exception e) { }
        return "";
    }

    public static String getLinuxRelease() {
        if (isLinux()) try {
            List<String> v = Files.readAllLines(Path.of("/etc/lsb-release"));
            for (String s:v) {
                String t[] = s.trim().split("=");
                if (t[0].trim().equals("DISTRIB_RELEASE")) {
                    s = t[1].trim();
                    if (s.startsWith("\"")) s=s.substring(1);
                    if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
                    return s;
                }
            }
        } catch (Exception e) { }
        return "";
    }

    public static String getLinuxDescription() {
        if (isLinux()) try {
            List<String> v = Files.readAllLines(Path.of("/etc/lsb-release"));
            for (String s:v) {
                String t[] = s.trim().split("=");
                if (t[0].trim().equals("DISTRIB_DESCRIPTION")) {
                    s = t[1].trim();
                    if (s.startsWith("\"")) s=s.substring(1);
                    if (s.endsWith("\""))   s=s.substring(0,s.length()-1);
                    return s;
                }
            }
        } catch (Exception e) { }
        return "";
    }

    public static boolean isWindows() {
        String bs = System.getProperty("os.name");
        if (bs!=null && bs.toLowerCase().startsWith("windows")) return true;
        return false;
    }

    public static boolean isLinux() {
        String bs = System.getProperty("os.name");
        if (bs!=null && bs.toLowerCase().startsWith("linux")) return true;
        return false;
    }

    public static String getBetriebssystem() {
        String ver = System.getProperty("os.version");
        if (isWindows()) return "Windows "+ver;
        if (isLinux())   return "Linux Kernel "+ver+" "+getLinuxDescription();
        return System.getProperty("os.name")+" "+ver;
    }

    public static String getJavaVendor() {
        String vendor="";
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vendor"); } catch (Exception e) {};
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.vendor"); } catch (Exception e) {};
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.specification.vendor"); } catch (Exception e) {};
        return vendor;
    }

    public static String getJavaVersionNumber() {
        String nr="";
        try { if (nr==null || nr.length()==0) nr = System.getProperty("java.runtime.version"); } catch (Exception e) {};
        try { if (nr==null || nr.length()==0) nr = System.getProperty("java.version"); } catch (Exception e) {};
        return nr;
    }

    public static String getJavaVersion() {
        return getJavaVendor() + " " + getJavaVersionNumber();
    }

    public static String getEncoding() {
        return System.getProperty("sun.jnu.encoding");
    }

    public static String getFileEncoding() {
        return System.getProperty("file.encoding");
    }

    public static String getIP() {
        return IP.getLocalIPString();
    }

    public static String getIPs() {
        return IP.getLocalIPsString();
    }

    public static String getHostname() {
        if (isWindows()) return System.getenv("COMPUTERNAME");
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            if (hostname!=null && hostname.length()>0) return hostname;
        } catch (Exception ex) {}
        return System.getenv("HOSTNAME");
    }
}
