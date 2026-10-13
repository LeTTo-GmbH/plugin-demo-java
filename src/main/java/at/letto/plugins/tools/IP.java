package at.letto.plugins.tools;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * einfache Methoden für die Verarbeitung von IP-Adressen
 */
public class IP {

    /**
     * Liest eine IPv4-Adresse aus vier dezimalen Oktetten.
     *
     * @param s IPv4-Adresse als Text
     * @return IPv4-Adresse oder {@code null} bei ungültiger Eingabe
     */
    public static InetAddress parseIP(String s) {
        s = s.trim();
        String st[] = s.split("\\.");
        try {
            if (st.length == 4) {
                byte b[] = new byte[4];
                for (int i = 0; i < 4; i++) {
                    if (st[i].length()<1) return null;
                    short x = Short.parseShort(st[i]);
                    if (x<0 || x>255) return null;
                    b[i] = (byte) x;
                }
                InetAddress inetAddress = InetAddress.getByAddress(b);
                return inetAddress;
            }
        }catch (Exception ex) { }
        return null;
    }

    /**
     * Prüft, ob der Text eine IPv4-Adresse enthält.
     *
     * @param s IPv4-Adresse als Text
     * @return {@code true}, wenn die IPv4-Adresse gelesen werden kann
     */
    public static boolean isIP(String s) {
        return parseIP(s)!=null;
    }

    /**
     * Prüft, ob eine IPv4-Adresse weder lokale, Link-Local-, Loopback- noch private Adresse ist.
     *
     * @param s IPv4-Adresse als Text
     * @return {@code true}, wenn keine dieser lokalen Adresskategorien vorliegt
     */
    public static boolean isOeffentlicheIP(String s) {
        InetAddress inetAddress = parseIP(s);
        if (inetAddress==null) return false;
        if (inetAddress.isAnyLocalAddress()) return false;
        if (inetAddress.isLinkLocalAddress()) return false;
        if (inetAddress.isLoopbackAddress()) return false;
        if (inetAddress.isSiteLocalAddress()) return false;
        return true;
    }

    /**
     * Ermittelt eine lokale IP-Adresse als Text.
     *
     * @return IP-Adresse oder Fehlermeldung, wenn keine gefunden wird
     */
    public static String getLocalIPString() {
        InetAddress iAddress = getLocalIP();
        if (iAddress!=null) {
            String ret = iAddress.getHostAddress();
            if (ret.contains("/")) {
                String[] r = ret.split("/");
                return r[r.length-1];
            }
            return ret;
        } else return "no IP-Adress found";
    }

    /**
     * Ermittelt eine lokale Adresse über Hostauflösung, Netzwerkverbindung oder Netzwerkschnittstellen.
     *
     * @return gefundene Adresse oder {@code null}
     */
    public static InetAddress getLocalIP() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            if (local!=null) return local;
        } catch (UnknownHostException e) { }
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            InetAddress local =  socket.getLocalAddress();
            if (local!=null) return local;
        } catch (Exception ex) {}
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i!=null &&
                        !i.isAnyLocalAddress() &&
                        !i.isLinkLocalAddress() &&
                        !i.isLoopbackAddress() &&
                        !i.isSiteLocalAddress()) return i;
                }
            }
        } catch (Exception ex) {}
        return null;
    }

    /**
     * Sammelt unterschiedliche lokale Adressen über Hostauflösung, Netzwerkverbindung und Schnittstellen.
     *
     * @return Liste der gefundenen Adressen
     */
    public static List<InetAddress> getLocalIPs() {
        List<InetAddress> iPs = new ArrayList<>();
        try {
            InetAddress local = InetAddress.getLocalHost();
            if (local!=null) iPs.add(local);
        } catch (UnknownHostException e) { }
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            InetAddress local = socket.getLocalAddress();
            if (local!=null) {
                boolean found=false;
                for (InetAddress ip : iPs)
                    if (ip.getHostAddress().equals(local.getHostAddress()))
                        found=true;
                if (!found) iPs.add(local);
            }
        } catch (Exception ex) {}
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i!=null &&
                            !i.isAnyLocalAddress() &&
                            !i.isLinkLocalAddress() &&
                            !i.isLoopbackAddress() &&
                            !i.isSiteLocalAddress()) {
                        boolean found=false;
                        for (InetAddress ip : iPs)
                            if (ip.getHostAddress().equals(i.getHostAddress()))
                                found=true;
                        if (!found) iPs.add(i);
                    }
                }
            }
        } catch (Exception ex) {}
        return iPs;
    }

    /**
     * Wandelt die gefundenen lokalen IP-Adressen in Texte um.
     *
     * @return Liste der Adresszeichenfolgen
     */
    public static List<String> getLocalIPsStringList() {
        List<InetAddress> iAddresses = getLocalIPs();
        List<String> iPs = new ArrayList<>();
        for (InetAddress iAddress : iAddresses) {
            if (iAddress!=null) {
                String ret = iAddress.getHostAddress();
                if (ret.contains("/")) {
                    String[] r = ret.split("/");
                    if (r[r.length-1].trim().length()>0)
                        iPs.add(r[r.length-1].trim());
                } else if (ret.trim().length()>0) iPs.add(ret.trim());
            }
        }
        return iPs;
    }

    /**
     * Verbindet die gefundenen lokalen IP-Adressen zu einem kommaseparierten Text.
     *
     * @return Adressliste als Text
     */
    public static String getLocalIPsString() {
        List<String> iPs = getLocalIPsStringList();
        String ret="";
        for (String ip:iPs) if (ip.trim().length()>0){
            ret += (ret.length()==0 ? "" : ", ")+ip;
        }
        return ret;
    }


    /**
     * Check, ob eine IP4-Adresse in einem definierten Range von IP-Adressbereichen liegt
     * @param range Definition von IP-Adressbereichen über
     *              StartIP-StopIP;nächsteStartIP-nächsteStopIP....<br>
     *              IP Adressen werden im 4stelligen Format xxx.xxx.xxx.xxx angegeben.<br>
     *              Das Trennzeichen für die Definition eines Bereiches is der Bindestrich - <br>
     *              Sollen mehrere Adressbereiche definiert werden, dann Trennzeichen Strichtpunkt verwenden.<br>
     *              Beispiel für IP-Range-Definition: 10.1.1.2-10.1.2.4;192.168.2.3-192.168.2.10;192.168.5.3-192.168.5.10
     * @param ip    IP-Adresse, die gecheckt werden soll
     * @return      true, wenn die ip-Adresse ip im Definitionsbereich der IP-Adressbereiche liegt
     */
    public static boolean checkIP(String range, String ip) {
        boolean check = false;
        for (String s : range.split(";")) {
            String[] x = s.split("-");
            if (x.length==2) {
                long startIP = getIpAsInt(x[0]);
                long stopIP = getIpAsInt(x[1]);
                if (startIP>0 && stopIP>0) {
                    long ipCurrent = getIpAsInt(ip);
                    if (startIP<=ipCurrent && ipCurrent<=stopIP)
                        check = true;
                }
            }
        }
        return check;
    }

    /**
     * Wandelt eine auflösbare IP-Adresse in einen ganzzahligen {@code long}-Wert um.
     *
     * @param ip    IP-Adresse in vierteiliger Form: 10.128.3.4
     * @return ganzzahliger Adresswert oder {@code -1}, wenn die Adresse nicht aufgelöst werden kann
     */
    public static long getIpAsInt(String ip) {
        if (ip == null || ip.trim().length()==0) return -1;
        ip=ip.trim();
        try {
            long result = 0;
            for (byte b: InetAddress.getByName(ip).getAddress())
                result = result << 8 | (b & 0xFF);
            return result;
        } catch (UnknownHostException e) {
            return -1;
        }
    }

}
