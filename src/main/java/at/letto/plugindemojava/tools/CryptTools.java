package at.letto.plugindemojava.tools;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CryptTools {

    /**
     * erzeugt eine MD5-Prüfsumme
     *
     * @param file Datei als Byte-Array
     * @return MD5 Prüfsumme
     */
    public static String md5(byte[] file) {
        String hash = "";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(file);
            for (int i = 0; i < digest.length; i++) {
                String p = (Integer.toHexString(digest[i] & 0xff));
                if (p.length()<2) p = "0"+p;
                hash += p;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * erzeugt eine MD5-Prüfsumme
     *
     * @param s String
     * @return MD5 Prüfsumme
     */
    public static String md5(String s) {
        return md5(s.getBytes());
    }

    /**
     * Erzeugt aus einem String eine md5-Prüfsumme für den Dateinamen<br>
     * Mit diesem Dateinamen sollte das Bild dann im Imageservice gespeichert und geladen werden können
     * @param base       Basis-String, der entweder der Dateiinhalt, oder eine eindeutige Beschreibung des Bildes darstellt.
     * @param extension  Extension der Datei
     * @return           Dateiname aus md5-Prüfsumme und Extension
     */
    public static String generateFilename(String base, String extension) {
        String md5 = md5(base);
        String filename = checkFilename(md5 + "." + extension);
        return filename;
    }

    /**
     * Entfernt aus einem Dateinamen alle Umlaute und Sonderzeichen.<br>
     * Es bleiben nur mehr Buchstaben, Zahlen, Minus und Unterstrich erhalten<br>
     * @param name alter Dateiname
     * @return     neuer Dateiname
     */
    public static String renameFile(String name) {
        name =  name.replaceAll("ö","oe").replaceAll("ü","ue").replaceAll("ä","ae").replaceAll("Ü","Ue").replaceAll("Ö","Oe").replaceAll("Ä","ae").replaceAll("ß","ss");
        name =  name.replaceAll("\\\\","-").replaceAll("/","-").replaceAll(" ","_").replaceAll("[^a-zA-Z0-9\\-_\\.]","");
        return name;
    }

    /**
     * Prüft einen Dateinamen ob er gültig ist und ändert ihn gegebenenfalls auf einen gültigen Namen
     * @param filename Dateiname
     * @return         gültiger Dateiname
     */
    public static String checkFilename(String filename) {
        Pattern p = Pattern.compile("^(.+)\\.([^\\.]*)$");
        Matcher m = p.matcher(filename);
        String name = filename;
        String ext = "ext";
        if (m.find()) {
            name = m.group(1);
            ext = m.group(2);
        }
        name = name.toLowerCase();
        ext = ext.toLowerCase().replaceAll("\\.", "");
        name = renameFile(name);
        ext = renameFile(ext);
        if (name.length() < 3) name = "file" + name;
        if (ext.length() < 1) ext = "ext";
        return name + "." + ext;
    }

    public static String loadFileAsBase64(File file) {
        if (file!=null && file.exists()) {
            String ImgString = "";
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                byte fileContent[] = new byte[(int)file.length()];
                fis.read(fileContent);
                byte[] ret = Base64.getEncoder().encode(fileContent);
                ImgString = new String(ret);
                fis.close();
                return ImgString;
            } catch (Exception ignored) {}
        }
        return "";
    }

}
