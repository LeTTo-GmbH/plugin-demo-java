package at.letto.plugindemojava.tools;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ServerStatus {

    public static Class               mainClass;
    public static ApplicationContext  mainContext;
    public static Manifest            mainManifest;


    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;
        private Worker(Process process) {
            this.process = process;
        }
        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }

    /**
     * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
     * es wieder beendet wird!
     * @param cmd     Kommando
     * @return        stdout des Programmes
     */
    public static String systemcall(String cmd) {
        String charset = Charset.defaultCharset().toString();
        return systemcall(cmd, charset);
    }

    /**
     * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
     * es wieder beendet wird!
     * @param cmd     Kommando
     * @param charset Character-Set
     * @return        stdout des Programmes
     */
    public static String systemcall(String cmd, String charset) {
        String ret="";
        Process p=null;
        try {
            String acmd = "";
            String h    = cmd;
            int    mode=0;      // 0 normal 1..innerhalb von Hochkomma
            int    f;

            /*
             * Zerlegen des Kommandos in die Parameterliste und Entfernen der Doppelhochkomma
             */
            Vector<String> cv = new Vector<String>();
            do {
                // Durchuche den String nach dem nächsten Vorkommen von " oder blank
                int pos      = h.length();
                char c       = 'a';
                f = h.indexOf("\""); if ((f>-1) && (f<pos)) { pos=f;c='"'; }
                f = h.indexOf(" "); if ((f>-1) && (f<pos)) { pos=f;c=' '; }
                if (c!='a') {
                    acmd += h.substring(0,pos);
                    h = h.substring(pos);
                }
                if (c=='"') {
                    if (mode==1) mode=0;
                    else         mode=1;
                    h = h.substring(1);
                } else if (c==' ') {
                    if (mode==1) {
                        acmd += " ";
                    } else {
                        if (acmd.length()>0) cv.add(acmd);
                        acmd="";
                    }
                    h = h.substring(1);
                }  else {
                    acmd += h;
                    h="";
                }
            } while (h.length()>0);
            if (acmd.length()>0) cv.add(acmd);
            String cmdlst[] = new String[cv.size()];
            for (int j=0;j<cv.size();j++) cmdlst[j] = cv.get(j);
            /**
             * Starten den Prozesses
             */
            p = new ProcessBuilder(cmdlst).start();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream(),charset));
            BufferedReader error =new BufferedReader(new InputStreamReader(p.getErrorStream(),charset));

            StringBuilder sbstdout = new StringBuilder();
            StringBuilder sberr    = new StringBuilder();
            String line;
            while ((line=reader.readLine())!=null) {
                sbstdout.append(line+"\n");
            }
            while ((line=error.readLine())!=null) {
                sberr.append(line+"\n");
            }
            p.waitFor();
            p.destroyForcibly();
            String err = sberr.toString();
            ret = sbstdout.toString();
            if (err.length()>0)
                ret += err;
        } catch(IOException e1) {
            return (ret.length()>0?"\n":"")+"Error: "+cmd+" kann nicht gestartet werden!";
        } catch(InterruptedException e2) {
            return (ret.length()>0?"\n":"")+"Error: "+cmd+" wurde unerwartet unterbrochen!";
        }
        return ret;
    }

    /**
     * Führt das Kommando cmd im Betriebssystem aus,
     * @param cmd     Kommando
     * @param timeout Zeit in ms nachdem das Programm sicher beendet wird!
     * @return        stdout des Programmes
     */
    public static String systemcall(String cmd,int timeout) {
        try {
            executeCommandLine(cmd,10000L);
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Führt einen Befehl in der Commandline aus<br>
     * Nach timeout bricht die Ausführung mit einer TimeoutException ab
     * @param commandLine     Befehl
     * @param timeout         Maximale Laufzeit in Millisekunden
     * @return Exitcode
     * @throws IOException      Fehler
     * @throws InterruptedException  Fehler
     * @throws TimeoutException Fehler
     */
    public static int executeCommandLine(final String commandLine,
                                         final long timeout)
            throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);
        // IO Streams definieren!
        BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader error =new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((reader.readLine())!=null) {

        }
        while ((error.readLine())!=null) {

        }
        // Worker für den Prozess definieren
        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null)
                return worker.exit;
            else
                throw new TimeoutException();
        } catch(InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }

    public String getJarFileName() {
        File file = getJarFile();
        if (file!=null) return file.getName();
        return "";
    }

    public File getJarFile() {
        String className = mainClass.getName().replace('.', '/');
        String classJar = mainClass.getResource("/" + className + ".class").toString();
        Matcher m;
        if ((m = Pattern.compile("file:([^\\!]+)\\!").matcher(classJar)).find()) {
            File file = new File(m.group(1));
            if (file.exists()) return file;
        }
        return null;
    }

    /** @return Liefert den Distributionsnamen der Linux Distribution wenn es sich um eine Linux-System handelt */
    public String getLinuxDistribution() {
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

    public String getLinuxRelease() {
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

    public String getLinuxDescription() {
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

    public boolean isWindows() {
        String bs = System.getProperty("os.name");
        if (bs!=null && bs.toLowerCase().startsWith("windows")) return true;
        return false;
    }

    public boolean isLinux() {
        String bs = System.getProperty("os.name");
        if (bs!=null && bs.toLowerCase().startsWith("linux")) return true;
        return false;
    }

    public boolean isUbuntu() {
        if (getLinuxDistribution().toLowerCase().trim().startsWith("ubuntu")) return true;
        return false;
    }

    public String getBetriebssystem() {
        String ver = System.getProperty("os.version");
        if (isWindows()) return "Windows "+ver;
        if (isLinux())   return "Linux Kernel "+ver+" "+getLinuxDescription();
        return System.getProperty("os.name")+" "+ver;
    }

    public String getJavaVendor() {
        String vendor="";
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vendor"); } catch (Exception e) {};
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.vendor"); } catch (Exception e) {};
        try { if (vendor==null || vendor.length()==0) vendor = System.getProperty("java.vm.specification.vendor"); } catch (Exception e) {};
        return vendor;
    }

    public String getJavaVersionNumber() {
        String nr="";
        try { if (nr==null || nr.length()==0) nr = System.getProperty("java.runtime.version"); } catch (Exception e) {};
        try { if (nr==null || nr.length()==0) nr = System.getProperty("java.version"); } catch (Exception e) {};
        return nr;
    }

    public String getJavaVersion() {
        return getJavaVendor() + " " + getJavaVersionNumber();
    }

    public String getEncoding() {
        return System.getProperty("sun.jnu.encoding");
    }

    public String getFileEncoding() {
        return System.getProperty("file.encoding");
    }

    public String getIP() {
        return IP.getLocalIPString();
    }

    public String getIPs() {
        return IP.getLocalIPsString();
    }

    public String getHostname() {
        if (isWindows()) return System.getenv("COMPUTERNAME");
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            if (hostname!=null && hostname.length()>0) return hostname;
        } catch (Exception ex) {}
        return System.getenv("HOSTNAME");
    }

    public List<String> getLibJars(){
        List<String> libs = new ArrayList<>();
        File jarFile = getJarFile();
        try {
            JarFile jf = new JarFile(jarFile);
            // Holen Sie sich die Enumeration der JAR-Einträge (Dateien und Verzeichnisse)
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("BOOT-INF/lib/")) name = name.substring(13);
                if (name.endsWith(".jar")) {
                    libs.add(name.substring(0,name.length()-4));
                }
            }
        } catch (Exception ex) {}
        Collections.sort(libs);
        return libs;
    }


    public String getUserDir() {
        return System.getProperty("user.dir");
    }

    public String getSystemHome() {
        String ret = System.getProperty("derby.system.home");
        if (ret==null) {
            File f = new File(".");
            if (f.exists()) {
                ret = f.getAbsolutePath();
                while (ret.endsWith("/") || ret.endsWith("\\"))    ret = ret.substring(0,ret.length()-1);
                while (ret.endsWith("/.") || ret.endsWith("\\.") ) ret = ret.substring(0,ret.length()-2);
            }
        }
        if (ret==null) {
            try {
                if (isLinux()) ret = systemcall("pwd").split("\\r?\\n")[0];
            } catch (Exception ex) {};
        }
        if (ret==null) ret = getUserDir();
        return ret;
    }

    public String getLanguage() {
        return System.getProperty("user.language");
    }

    public String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    public String getServerUsername() {
        return System.getProperty("user.name");
    }

    public String getJavaSpecificationVersion() {
        return System.getProperty("java.specification.version");
    }

    public String getTomeeRevision() {
        Matcher m;
        String version = "";
        ApplicationContext context = mainContext;
        TomcatWebServer tomcatWebServer = (TomcatWebServer) ((AnnotationConfigServletWebServerApplicationContext) context).getWebServer();
        Tomcat tomcat = tomcatWebServer.getTomcat();
        String servername = tomcat.getService().getName();
        String implementationVersion = tomcat.getClass().getPackage().getImplementationVersion();
        if (implementationVersion==null || implementationVersion.trim().length()==0) {
            List<String> libs = getLibJars();
            Pattern p = Pattern.compile("^(tomcat-.*)-([0-9\\.]+)");
            for (String lib:libs) {
                if ((m=p.matcher(lib)).find()) {
                    if (m.group(1).equals("tomcat-embed-core"))
                        return m.group(2);
                    version=m.group(2);
                }
            }
        }
        return version;
    }


}
