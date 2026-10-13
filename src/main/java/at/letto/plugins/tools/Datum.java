package at.letto.plugins.tools;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hilfsmethoden zum Parsen, Formatieren und Umrechnen von Datum, Uhrzeit und Zeitdauer.
 */
public class Datum {

    /**
     * Datumsformat {@code yyyy-MM-dd}.
     */
    public static SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Datums- und Zeitformat {@code yyyy-MM-dd HH:mm:ss}.
     */
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Datums- und Zeitformat mit Millisekunden.
     */
    public static SimpleDateFormat simpleDateFormatMillis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * Zeitformat {@code HH:mm:ss}.
     */
    public static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * Englische Wochentagsnamen mit Sonntag als erstem Eintrag mit drei Zeichen.
     */
    public static String[] WEEKDAYS3 = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    /**
     * Englische Wochentagsnamen mit Sonntag als erstem Eintrag mit zwei Zeichen.
     */
    public static String[] WEEKDAYS2 = {"Su","Mo","Tu","We","Th","Fr","Sa"};
    /**
     * Englische Wochentagsnamen mit Sonntag als erstem Eintrag.
     */
    public static String[] WEEKDAYS = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    /**
     * Deutsche Wochentagsnamen mit Sonntag als erstem Eintrag.
     */
    public static String[] WEEKDAYSGERMAN = {"Sonntag","Montag","Dinestag","Mittwock","Donnerstag","Freitag","Samstag"};
    /**
     * Deutsche Wochentagsnamen mit Sonntag als erstem Eintrag mit drei Zeichen.
     */
    public static String[] WEEKDAYSGERMAN3 = {"Son","Mon","Die","Mit","Don","Fre","Sam"};
    /**
     * Deutsche Wochentagsnamen mit Sonntag als erstem Eintrag mit zwei Zeichen.
     */
    public static String[] WEEKDAYSGERMAN2 = {"So","Mo","Di","Mi","Do","Fr","Sa"};

    /**
     * Englische Monatsnamen mit Januar als erstem Eintrag mit drei Zeichen.
     */
    public static String[] MONTH3 = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    /**
     * Deutsche Monatsnamen mit Januar als erstem Eintrag mit drei Zeichen.
     */
    public static String[] MONTHGERMAN3 = {"Jän","Feb","Mär","Apr","Mai","Jun","Jul","Aug","Sep","Okt","Nov","Dez"};
    /**
     * Englische Monatsnamen mit Januar als erstem Eintrag.
     */
    public static String[] MONTH = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    /**
     * Deutsche Monatsnamen mit Januar als erstem Eintrag.
     */
    public static String[] MONTHGERMAN = {"Jänner","Februar","März","April","Mai","Juni","Juli","August","September","Oktober","November","Dezember"};

    /**
     * Muster für ein Datum als {@code yyyyMMdd}.
     */
    public static Pattern patternDateYYYYMMDD = Pattern.compile("^(?<y>\\d\\d\\d\\d)(?<m>\\d\\d)(?<d>\\d\\d)$");
    /**
     * Muster für Jahr, Monat und Tag mit optionalen Trennzeichen.
     */
    public static Pattern patternDateYYYYMD = Pattern.compile("^(?<y>\\d\\d\\d\\d)[\\.\\-\\/\\s]*(?<m>\\d+)[\\.\\-\\/\\s]*(?<d>\\d+)$");
    /**
     * Muster für Tag, Monat und Jahr mit optionalen Trennzeichen.
     */
    public static Pattern patternDateDMY = Pattern.compile("^(?<d>\\d+)[\\.\\-\\/\\s]*(?<m>\\d+)[\\.\\-\\/\\s]*(?<y>\\d+)$");

/*
    static String dateFormats[] = {"d.M.yyyy","d.MMM.yyyy","d/M/yyyy","d/MMM/yyyy","d-M-yyyy","d-MMM-yyyy","yyyy.M.d", "yyyy.MMM.d",
            "yyyy/M/d","yyyy/MMM/d","yyyy-M-d","yyyy-MMM-d","d.M.yy","d.MMM.yy","yy-M-d","yy-MMM-d",
            "d.MMM.y","y-M-d","y-MMM-d"};*/

    /**
     * Bestimmt die Sekunde innerhalb der Minute.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Sekunde von 0 bis 59
     */
    public static int second(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    /**
     * Bestimmt die Minute innerhalb der Stunde.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Minute von 0 bis 59
     */
    public static int minute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * Stunde im 24-Stunden-Format
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Stunde von 0 bis 23
     */
    public static int hour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Bestimmt den Tag innerhalb des Monats.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Tag des Monats von 1 bis 31
     */
    public static int day(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
        // return date.getDate();
    }

    /**
     * Bestimmt den Monat des Datums.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Monat von 1 bis 12
     */
    public static int month(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;
        //return date.getMonth()+1;
    }

    /**
     * Bestimmt das Jahr des Datums.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Jahr des Datums
     */
    public static int year(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
        //return date.getYear()+1900;
    }

    /**
     * Bestimmt den Wochentag mit Sonntag als erstem Tag.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentag von 0 (Sonntag) bis 6 (Samstag)
     */
    public static int dayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * Liefert die englische Wochentagsbezeichnung mit drei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung mit drei Zeichen
     */
    public static String dayOfWeekString3(Date date) { return WEEKDAYS3[dayOfWeek(date)]; }
    /**
     * Liefert die englische Wochentagsbezeichnung mit zwei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung mit zwei Zeichen
     */
    public static String dayOfWeekString2(Date date) { return WEEKDAYS2[dayOfWeek(date)]; }
    /**
     * Liefert die englische Wochentagsbezeichnung.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung
     */
    public static String dayOfWeekString(Date date) { return WEEKDAYS[dayOfWeek(date)]; }
    /**
     * Liefert die deutsche Wochentagsbezeichnung mit drei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung mit drei Zeichen
     */
    public static String dayOfWeekStringGerman3(Date date) { return WEEKDAYSGERMAN3[dayOfWeek(date)]; }
    /**
     * Liefert die deutsche Wochentagsbezeichnung mit zwei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung mit zwei Zeichen
     */
    public static String dayOfWeekStringGerman2(Date date) { return WEEKDAYSGERMAN2[dayOfWeek(date)]; }
    /**
     * Liefert die deutsche Wochentagsbezeichnung.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Wochentagsbezeichnung
     */
    public static String dayOfWeekStringGerman(Date date) { return WEEKDAYSGERMAN[dayOfWeek(date)]; }

    /**
     * Liefert die englische Monatsbezeichnung mit drei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Monatsbezeichnung mit drei Zeichen
     */
    public static String monthString3(Date date) { return MONTH3[month(date)-1];}
    /**
     * Liefert die englische Monatsbezeichnung.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Monatsbezeichnung
     */
    public static String monthString(Date date) { return MONTH[month(date)-1];}
    /**
     * Liefert die deutsche Monatsbezeichnung mit drei Zeichen.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Monatsbezeichnung mit drei Zeichen
     */
    public static String monthStringGerman3(Date date) { return MONTHGERMAN3[month(date)-1];}
    /**
     * Liefert die deutsche Monatsbezeichnung.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Monatsbezeichnung
     */
    public static String monthStringGerman(Date date) { return MONTHGERMAN[month(date)-1];}


    /**
     * Liefert den aktuellen Zeitpunkt.
     *
     * @return aktueller Zeitpunkt
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Formatiert ein Datum als {@code d.M.yyyy}.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Datumstext oder leerer String bei {@code null}
     */
    public static String toString(Date date) {
        if (date==null) return "";
        return day(date)+"."+month(date)+"."+year(date);
    }

    /**
     * Formatiert ein Datum als {@code dd/MM/yyyy}.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Datumstext oder leerer String bei {@code null}
     */
    public static String toHTMLDateString(Date date) {
        if (date==null) return "";
        String y=year(date)+"";
        while (y.length()<4) y="0"+y;
        String m=month(date)+"";
        while (m.length()<2) m="0"+m;
        String d=day(date)+"";
        while (d.length()<2) d="0"+d;
        return d+"/"+m+"/"+y;
    }

    /**
     * Formatiert ein Datum als {@code yyyy-MM-dd}.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Datumstext
     */
    public static String toSQLDateString(Date date) {
        if (date==null) return "";
        String y=year(date)+"";
        while (y.length()<4) y="0"+y;
        String m=month(date)+"";
        while (m.length()<2) m="0"+m;
        String d=day(date)+"";
        while (d.length()<2) d="0"+d;
        return y+"-"+m+"-"+d;
    }

    /**
     * Formatiert ein Datum als {@code yyyy-MM-dd}.
     *
     * @param year Jahr
     * @param month Monat von 1 bis 12
     * @param day Tag des Monats
     * @return Datumstext
     */
    public static String toSQLDateString(int year, int month, int day) {
        String y=year+"";
        while (y.length()<4) y="0"+y;
        String m=month+"";
        while (m.length()<2) m="0"+m;
        String d=day+"";
        while (d.length()<2) d="0"+d;
        return y+"-"+m+"-"+d;
    }

    /**
     * Erstellt ein SQL-Datum aus Jahr, Monat und Tag.
     *
     * @param y Jahr
     * @param m Monat von 1 bis 12
     * @param d Tag des Monats
     * @return Datum oder {@code null}, wenn die Werte nicht verarbeitet werden können
     */
    public static Date setDate(int y, int m, int d) {
        if (y<0 || m<1 || d<1) return null;
        if (y>9999) return null;
        if (m>12) return null;
        if (d>31) return null;
        try {
            Date date = java.sql.Date.valueOf(toSQLDateString(y, m, d));
            return date;
        } catch (Exception ex) { return null; }
    }

    /**
     * Liest ein Datum in den unterstützten numerischen oder textuellen Formaten.
     *
     * @param s Zeit- oder Datumstext
     * @return Datum oder {@code null}, wenn kein Format passt
     */
    public static Date parse(String s) {
        Matcher matcher;
        int y,m,d;
        s = s.trim();
        if ((matcher=patternDateYYYYMMDD.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        if ((matcher=patternDateYYYYMD.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        if ((matcher=patternDateDMY.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        return parseDate(s);
    }

    /**
     * Unterstützte Parser für Datumsangaben.
     */
    static SimpleDateFormat formatter[] = {
            new SimpleDateFormat("dd/MM/yyyy"),
            new SimpleDateFormat("dd.MM.yyyy"),
            new SimpleDateFormat("dd-MM-yyyy"),
            new SimpleDateFormat("dd.MMM.yyyy"),
            new SimpleDateFormat("dd-MMM-yyyy"),
            new SimpleDateFormat("yyyy-MMM-dd"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy.MMM.dd")
    };

    /**
     * Probiert die hinterlegten SimpleDateFormat-Muster zum Lesen eines Datums.
     *
     * @param dat zu prüfender beziehungsweise zu lesender Datumstext
     * @return Datum oder {@code null}, wenn kein Muster passt
     */
    private static Date parseDate(String dat) {
        Date date = null;
        for (SimpleDateFormat format: formatter) {
            try {
                date = format.parse(dat);
                return date;
            } catch (Exception e) {}
        }
        return null;
    }

    // ------------------------------------------------------- parse DATE -----------------------------
    /**
     * Regulärer Ausdruck für Uhrzeiten mit optionalen Sekundenbruchteilen.
     */
    private static final String  pTime = "\\s*(?<h>\\d\\d?):(?<m>\\d\\d?)(:(?<s>\\d\\d?)(\\.(?<nk>\\d+?)?)?)?\\s*";
    /**
     * Muster für eine alleinstehende Uhrzeit.
     */
    private static final Pattern patternTime=Pattern.compile("^"+pTime+"$");
    /**
     * Muster für Uhrzeit vor Datum.
     */
    private static final Pattern patternTimeDate=Pattern.compile("^(?<time>"+pTime+")\\s(?<date>[a-zA-Z0-9]+.*)$");
    /**
     * Muster für Datum vor Uhrzeit.
     */
    private static final Pattern patternDateTime=Pattern.compile("^(?<date>[a-zA-Z0-9]+.*)\\s(?<time>"+pTime+")$");

    /**
     * Addiert Kalendertage zu einem Datum.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @param days Anzahl der zu addierenden Tage
     * @return Datum nach Addition der Tage
     */
    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date = c.getTime();
        return date;
    }

    /**
     * Konvertiert den Zeitpunkt zwischen Date und LocalDateTime in der Systemzeitzone.
     *
     * @param dateToConvert zu konvertierender Zeitpunkt, darf {@code null} sein
     * @return konvertierter Wert oder {@code null} bei {@code null} als Eingabe
     */
    public static LocalDateTime localDate(Date dateToConvert) {
        if (dateToConvert==null) return null;
        return LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Konvertiert den Zeitpunkt zwischen Date und LocalDateTime in der Systemzeitzone.
     *
     * @param dateToConvert zu konvertierender Zeitpunkt, darf {@code null} sein
     * @return konvertierter Wert oder {@code null} bei {@code null} als Eingabe
     */
    public static Date localDate(LocalDateTime dateToConvert) {
        if (dateToConvert==null) return null;
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Rechnet eine Uhrzeit in Sekunden um; Stunden und Minuten werden nicht auf Wertebereiche geprüft.
     *
     * @param s Zeitstring 10:30 bzw. 10:30:22 bzw. 10:30:22.435423
     * @return Zeit in Sekunden oder {@code 0}, wenn das Format nicht erkannt wird
     */
    public static double parseTime(String s) {
        s=s.trim();
        Matcher m;
        double h=0, min=0, sek=0;
        if ((m=Pattern.compile("^(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Double.parseDouble(m.group(1));
            min = Double.parseDouble(m.group(2));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+\\.?\\d*)$").matcher(s)).find()) {
            h = Double.parseDouble(m.group(1));
            min = Double.parseDouble(m.group(2));
            sek = Double.parseDouble(m.group(3));
        }
        sek = h*3600d+min*60+sek;
        return sek;
    }

    /**
     * Zerlegt eine Uhrzeit in Stunden, Minuten, Sekunden und Millisekunden.
     *
     * @param s Zeitstring 10:30 bzw. 10:30:22 bzw. 10:30:22.435423
     * @return Array mit Stunden, Minuten, Sekunden und Millisekunden oder {@code null} bei einem unbekannten Format
     */
    public static int[] parseTimeArray(String s) {
        s=s.trim();
        Matcher m;
        int h=0, min=0, sek=0, millis=0;
        if ((m=Pattern.compile("^(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
            sek = Integer.parseInt(m.group(3));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+)\\.(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
            sek = Integer.parseInt(m.group(3));
            String ms = m.group(4);
            if (ms.length()>3) ms = ms.substring(0,3);
            while (ms.length()<3) ms = ms+"0";
            millis = Integer.parseInt(ms);
        } else return null;
        int[] result = new int[4];
        result[0] = h;
        result[1] = min;
        result[2] = sek;
        result[3] = millis;
        return result;
    }

    /**
     * Formatiert ein Datum als {@code yyyy-MM-dd}.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return Datumstext oder leerer String bei {@code null}
     */
    public static String formatSimple(Date date) {
        if (date==null) return "";
        return simpleDate.format(date);
    }

    /**
     * Formatiert Datum und Uhrzeit im Standardformat.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatDateTime(Date date) {
        if (date==null) return "";
        return simpleDateFormat.format(date);
    }

    /**
     * Liefert das aktuelle Datum in der Systemzeitzone.
     *
     * @return aktuelles lokales Datum
     */
    public static LocalDate nowLocalDate() { return LocalDate.now(); }
    /**
     * Liefert Datum und Uhrzeit in der Systemzeitzone.
     *
     * @return aktuelles lokales Datum mit Uhrzeit
     */
    public static LocalDateTime nowLocalDateTime() { return LocalDateTime.now(); }

    /**
     * Formatiert Datum und Uhrzeit im Standardformat.
     *
     * @param localDateTime lokales Datum mit Uhrzeit
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime==null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * Formatiert die Uhrzeit im Standardformat.
     *
     * @param localDateTime lokales Datum mit Uhrzeit
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatTime(LocalDateTime localDateTime) {
        if (localDateTime==null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Formatiert Datum und Uhrzeit im Standardformat.
     *
     * @param localDate lokales Datum
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatDateTime(LocalDate localDate) {
        if (localDate==null) return "";
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Formatiert Datum und Uhrzeit mit dem angegebenen Muster beziehungsweise der Sprachkennung.
     *
     * @param localDateTime lokales Datum mit Uhrzeit
     * @param format Formatmuster oder Sprachkennung wie {@code de_AT} beziehungsweise {@code en_US}
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatDateTime(LocalDateTime localDateTime, String format) {
        if (localDateTime==null) return "";
        String formatString = "yyyy-MM-dd HH:mm:ss";
        if (format.matches("\\w\\w[\\-_]\\w\\w")) {
            if (format.toLowerCase().startsWith("de")) formatString = "d.M.yyyy HH:mm:ss";
        } else if (format.length()>0) formatString = format;
        return localDateTime.format(DateTimeFormatter.ofPattern(formatString));
    }
    /**
     * Formatiert die Uhrzeit mit dem angegebenen Muster beziehungsweise der Sprachkennung.
     *
     * @param localDateTime lokales Datum mit Uhrzeit
     * @param format Formatmuster oder Sprachkennung wie {@code de_AT} beziehungsweise {@code en_US}
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatTime(LocalDateTime localDateTime, String format) {
        if (localDateTime==null) return "";
        String formatString = "HH:mm:ss";
        if (format.matches("\\w\\w[\\-_]\\w\\w")) {
            if (format.toLowerCase().startsWith("de")) formatString = "HH:mm:ss";
        } else if (format.length()>0) formatString = format;
        return localDateTime.format(DateTimeFormatter.ofPattern(formatString));
    }

    /**
     * Formatiert Datum und Uhrzeit mit dem angegebenen Muster beziehungsweise der Sprachkennung.
     *
     * @param localDate lokales Datum
     * @param format Formatmuster oder Sprachkennung wie {@code de_AT} beziehungsweise {@code en_US}
     * @return formatierter Text oder leerer String bei {@code null}
     */
    public static String formatDateTime(LocalDate localDate, String format) {
        if (localDate==null) return "";
        String formatString = "yyyy-MM-dd";
        if (format.matches("\\w\\w[\\-_]\\w\\w")) {
            if (format.toLowerCase().startsWith("de")) formatString = "d.M.yyyy";
        } else if (format.length()>0) formatString = format;
        return localDate.format(DateTimeFormatter.ofPattern(formatString));
    }

    /**
     * Formatiert einen Datum-Integer im Standardformat.
     *
     * @param d       Datum-Integer
     * @return        formatierter Datum-String mit Zeit
     *
     */
    public static String formatDateTime(long d) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatDateTime(localDateTime);
    }
    /**
     * Formatiert einen Datum-Integer im Standardformat.
     *
     * @param d       Datum-Integer
     * @return        formatierter Datum-String
     *
     */
    public static String formatDate(long d) {
        LocalDate localDate = dateIntegerToLocalDate(d);
        return formatDateTime(localDate);
    }
    /**
     * Formatiert einen Datum-Integer im Standardformat.
     *
     * @param d       Datum-Integer
     * @return        formatierter Zeit-String
     *
     */
    public static String formatTime(long d) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatTime(localDateTime);
    }

    /** Formatieren eines Datum-Integers nach Locale
     * @param d       Datum-Integer
     * @param format  Formatstring z.B: "yyyy-MM-dd" oder Locale z.B. "de_AT","en_US"
     * @return        formatierter Datum-String mit Zeit
     */
    public static String formatDateTime(long d, String format) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatDateTime(localDateTime,format);
    }
    /** Formatieren eines Datum-Integers nach Locale
     * @param d       Datum-Integer
     * @param format  Formatstring z.B: "yyyy-MM-dd" oder Locale z.B. "de_AT","en_US"
     * @return        formatierter Datum-String
     */
    public static String formatDate(long d, String format) {
        LocalDate localDate = dateIntegerToLocalDate(d);
        return formatDateTime(localDate,format);
    }
    /** Formatieren eines Datum-Integers nach Locale
     * @param d       Datum-Integer
     * @param format  Formatstring z.B: "yyyy-MM-dd" oder Locale z.B. "de_AT","en_US"
     * @return        formatierter Zeit-String
     */
    public static String formatTime(long d, String format) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatTime(localDateTime,format);
    }

    /**
     * Bestimmt die Sekunden seit dem 1. Januar des Jahres 0 für die aktuelle lokale Zeit.
     *
     * @return aktueller Datum-Integer in Sekunden
     */
    public static long nowDateInteger() { return toDateInteger(LocalDateTime.now()); }

    // -------------------------------------------------------------------------------------------------------------
    /**
     * Unterstützte Datumsformate für die LocalDateTime-Parser.
     */
    static String dateFormats[] = {"d.M.yyyy","d.MMM.yyyy","d/M/yyyy","d/MMM/yyyy","d-M-yyyy","d-MMM-yyyy","yyyy.M.d", "yyyy.MMM.d",
                                   "yyyy/M/d","yyyy/MMM/d","yyyy-M-d","yyyy-MMM-d","d.M.yy","d.MMM.yy","yy-M-d","yy-MMM-d",
                                   "d.MMM.y","y-M-d","y-MMM-d"};
    /**
     * Unterstützte Uhrzeitformate für die LocalDateTime-Parser.
     */
    static String timeFormats[] = {"H:m:s","H:m","H:m:s.S","H:m:s.SS","H:m:s.n"};

    /**
     * Parser für die Kombinationen unterstützter Datums- und Zeitformate.
     */
    private static DateTimeFormatter[] formatterLDT=initFormatterLDT();

    /**
     * Erzeugt Parser für die Kombinationen der unterstützten Datums- und Zeitformate.
     *
     * @return Parser für Datum vor Uhrzeit und Uhrzeit vor Datum
     */
    public static DateTimeFormatter[] initFormatterLDT() {
        Vector<String> fo = new Vector<String>();
        for (int i=0;i<dateFormats.length;i++) {
            for (int j=0;j<timeFormats.length;j++) {
                fo.add(dateFormats[i]+" "+timeFormats[j]);
                fo.add(timeFormats[j]+" "+dateFormats[i]);
            }
        }
        DateTimeFormatter[] f=new DateTimeFormatter[fo.size()];
        for (int i=0;i<fo.size();i++)
            f[i] = DateTimeFormatter.ofPattern(fo.get(i));
        return f;
    }

    /**
     * Liest Datum und Uhrzeit mit den unterstützten Formaten; ein {@code T} wird als Leerzeichen behandelt.
     *
     * @param dat zu prüfender beziehungsweise zu lesender Datumstext
     * @return Datum und Uhrzeit oder {@code null}, wenn kein Format passt
     */
    public static LocalDateTime parseLocalDateTime(String dat) {
        LocalDateTime date = null;
        String datumstring = dat.replaceAll("T"," ");
        for (DateTimeFormatter format: formatterLDT) try {
            TemporalAccessor dx = format.parse(datumstring);
            return LocalDateTime.parse(datumstring,format);
        } catch (Exception e) {}
        return null;
    }

    // Prüft ob es sich bei der Eingabe um ein Datum mit Zeit handelt
    /**
     * Prüft, ob die Eingabe als Datum mit Uhrzeit erkannt wird.
     *
     * @param dat zu prüfender beziehungsweise zu lesender Datumstext
     * @return {@code true}, wenn Datum und Uhrzeit erkannt werden
     */
    public static boolean isDateTime(String dat) {
        Matcher m;
        try {
            if ((m = patternDateTime.matcher(dat)).matches()) {
                String d = m.group(1);
                String t = m.group(2);
                if (isDate(d) && isTime(t)) return true;
            }
            if ((m = patternTimeDate.matcher(dat)).matches()) {
                String t = m.group(1);
                String d = m.group(2);
                if (isDate(d) && isTime(t)) return true;
            }
        } catch (Exception e) {}
        return false;
    }
    //** Prüft ob es sich bei der Eingabe um ein Datum handelt
    /**
     * Prüft, ob die Eingabe als Datum ohne Uhrzeit erkannt wird.
     *
     * @param dat zu prüfender beziehungsweise zu lesender Datumstext
     * @return {@code true}, wenn ein Datumsformat erkannt wird
     */
    public static boolean isDate(String dat) {
        try {
            Matcher m;
            if (isTime(dat)) return false;
            if (isDateTime(dat)) return false;
            if (parseDate(dat)!=null) return true;
        } catch (Exception e) {}
        return false;
    }
    /**
     * Prüft ob es sich bei der Eingabe um eine Zeit handelt
     *
     * @param dat zu prüfender beziehungsweise zu lesender Datumstext
     * @return {@code true}, wenn das Uhrzeitformat passt; Wertebereiche werden nicht geprüft
     */
    public static boolean isTime(String dat) {
        try {
            Matcher m;
            if ((m=patternTime.matcher(dat)).matches()) {
                double t = parseTime(dat);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    // -------------------------------------------------------
    /**
     * Bestimmt die Tagesanzahl vom 1.1.0000 bis zum angegebenen Datum
     *
     * @param y Jahr
     * @param m Monat von 1 bis 12
     * @param d Tag des Monats
     * @return Tage zwischen Referenzdatum und angegebenem Datum
     */
    public static long calcDays(int y, int m, int d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        LocalDate date    = LocalDate.of(y,m,d);
        long days = ChronoUnit.DAYS.between(refdate,date);
        return days;
    }


    /**
     * Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums
     *
     * @param d Datum beziehungsweise Zeitpunkt
     * @return Datum-Integer in Sekunden
     */
    public static long toDateInteger(Date d) {
        return toDateInteger(year(d), month(d), day(d), hour(d), minute(d), second(d));
    }

    /**
     * Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums
     *
     * @param d Datum beziehungsweise Zeitpunkt
     * @return Datum-Integer in Sekunden
     */
    public static long toDateInteger(LocalDate d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        long days = ChronoUnit.DAYS.between(refdate,d);
        long ret  = days*3600L*24L;
        return ret;
    }

    /**
     * Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums
     *
     * @param d Datum beziehungsweise Zeitpunkt
     * @return Datum-Integer in Sekunden
     */
    public static long toDateInteger(LocalDateTime d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        long days = ChronoUnit.DAYS.between(refdate,d);
        long t    = d.getHour()*3600+d.getMinute()*60+d.getSecond();
        long ret = days*3600L*24L+t;
        return ret;
    }

    /**
     * Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums als String
     *
     * @param s Zeit- oder Datumstext
     * @return Datum-Integer in Sekunden
     * @throws RuntimeException wenn der Datumstext nicht verarbeitet werden kann
     */
    public static long toDateInteger(String s) {
        if (isTime(s)) {
            int[] time = parseTimeArray(s);
            if (time!=null)
                return toDateInteger(0,0,0,time[0],time[1],time[2]);
        } else if (isDate(s)) {
            return toDateInteger(parse(s));
        } else if (isDateTime(s)) {
            Matcher m;
            String d,t;
            if ((m = patternDateTime.matcher(s)).matches()) {
                d = m.group(1);
                t = m.group(2);
            } else if ((m = patternTimeDate.matcher(s)).matches()) {
                t = m.group(1);
                d = m.group(2);
            } else throw new RuntimeException("cannot parse Date");
            int[] time = parseTimeArray(t);
            long rt = toDateInteger(0,1,1,time[0],time[1],time[2]);
            if (time[3]>499) rt++;
            long rd = toDateInteger(parse(d));
            return rt + rd;
        }
        throw new RuntimeException("cannot parse Date");
    }

    /**
     * Bestimmt die Sekunden seit dem 1. Januar des Jahres 0.
     *
     * @param y Jahr
     * @param m Monat von 1 bis 12
     * @param d Tag des Monats
     * @param h Stunde
     * @param min Minute
     * @param sec Sekunde
     * @return Datum-Integer in Sekunden
     */
    public static long toDateInteger(int y, int m, int d, int h, int min, int sec) {
        long days = calcDays(y,m,d);
        long t    = h*3600+min*60+sec;
        long ret  = days*3600L*24L+t;
        return ret;
    }

    /**
     * Konvertiert einen Datum-Integer in ein Datum.
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Datum ohne Uhrzeit
     */
    public static LocalDate dateIntegerToLocalDate(long d){
        long days = d/3600/24;
        long time = d%(3600*24);
        LocalDate refdate = LocalDate.of(0, 1, 1);
        LocalDate date = refdate.plusDays(days);
        return date;
    }

    /**
     * Konvertiert einen Datum-Integer in Datum und Uhrzeit.
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Datum und Uhrzeit
     */
    public static LocalDateTime dateIntegerToLocalDateTime(long d){
        long days = d/3600/24;
        long time = d%(3600*24);
        LocalDateTime refdate = LocalDateTime.of(0, 1, 1,0,0,0);
        LocalDateTime date = refdate.plusDays(days).plusSeconds(time);
        return date;
    }

    /**
     * Bestimmt das Jahr eines Datum-Integers
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Jahr des Datums
     */
    public static int year(long d)  { return dateIntegerToLocalDate(d).getYear();}
    /**
     * Bestimmt das Monat eines Datum-Integers
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Monat von 1 bis 12
     */
    public static int month(long d)  { return dateIntegerToLocalDate(d).getMonthValue();}
    /**
     * Bestimmt den Tag im Monat eines Datum-Integers
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Tag des Monats von 1 bis 31
     */
    public static int day(long d)    { return dateIntegerToLocalDate(d).getDayOfMonth();}
    /**
     * Bestimmt die Stunde eines Datum-Integers
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Stunde von 0 bis 23
     */
    public static int hour(long d)    { return dateIntegerToLocalDateTime(d).getHour();}
    /**
     * Bestimmt die Minute eines Datum-Integers
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Minute von 0 bis 59
     */
    public static int minute(long d)    { return dateIntegerToLocalDateTime(d).getMinute();}
    /**
     * Bestimmt die Sekunde innerhalb der Minute.
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Sekunde von 0 bis 59
     */
    public static int second(long d)    { return dateIntegerToLocalDateTime(d).getSecond();}
    /**
     * Bestimmt den Wochentag eines Datum-Integers als Mo=1 bis So=7
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return Wochentag von 1 (Montag) bis 7 (Sonntag)
     */
    public static int weekday(long d) { return dateIntegerToLocalDate(d).getDayOfWeek().getValue();}
    /**
     * Bestimmt die Wochenzahl mit der im Projekt hinterlegten Jahresanfangsberechnung.
     *
     * @param d Datum-Integer in Sekunden seit dem 1. Januar des Jahres 0
     * @return berechnete Wochenzahl; keine ISO-Kalenderwoche
     */
    public static int week(long d)    { return week(dateIntegerToLocalDate(d)); }
    /**
     * Bestimmt die Wochenzahl mit der im Projekt hinterlegten Jahresanfangsberechnung.
     *
     * @param date Datum beziehungsweise Zeitpunkt
     * @return berechnete Wochenzahl; keine ISO-Kalenderwoche
     */
    public static int week(LocalDate date) {
        // Wochentag des ersten Jänners im Jahr
        int weekdayErsterJan = LocalDate.of(date.getYear(),1,1).getDayOfWeek().getValue();
        int doy= date.getDayOfYear();
        int offset = (weekdayErsterJan+5)%7;
        int week = (doy+offset)/7;
        return week;
    }

    /**
     * parst eine Zeitdauer-Angabe in einen Sekunden Long-Wert
     * @param s Zeitdauer
     * @return  Zeitdauer in Sekunden
     */
    public static long parseDauer(String s) {
        Matcher m;
        s=s.trim();
        if (s.matches("^\\d+$")) {
            return Long.parseLong(s);
        }
        String ps1 = "^\\s*((?<y>\\d*)\\s*(y|years?|j|jahre?))?";
        ps1 +=       "\\s*,?\\s*((?<m>\\d*)\\s*(m|months?|monate?))?";
        ps1 +=       "\\s*,?\\s*((?<d>\\d*)\\s*(d|days?|t|tage?))?";
        ps1 +=       "\\s*,?\\s*((((?<h>\\d?\\d):)?((?<min>\\d?\\d):))?(?<s>\\d?\\d)(\\.(?<ts>\\d*))?)?\\s*$";
        long year=0,month=0,day=0,hour=0,min=0,sec=0,tsec=0;
        String ps2 = "^\\s*((?<y>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*((?<m>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*((?<d>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*,?\\s*((((?<h>\\d?\\d):)?((?<min>\\d?\\d):))?(?<s>\\d?\\d)(\\.(?<ts>\\d*))?)?\\s*$";
        String[] psa = {ps1,ps2};
        for (String ps:psa) {
            if ((m = Pattern.compile(ps).matcher(s)).find()) {
                try {
                    year = Long.parseLong(m.group("y"));
                } catch (Exception ignore) {
                }
                try {
                    month = Long.parseLong(m.group("m"));
                } catch (Exception ignore) {
                }
                try {
                    day = Long.parseLong(m.group("d"));
                } catch (Exception ignore) {
                }
                try {
                    hour = Long.parseLong(m.group("h"));
                } catch (Exception ignore) {
                }
                try {
                    min = Long.parseLong(m.group("min"));
                } catch (Exception ignore) {
                }
                try {
                    sec = Long.parseLong(m.group("s"));
                } catch (Exception ignore) {
                }
                try {
                    String ts = m.group("ts").substring(0, 3);
                    while (ts.length() < 3) ts += "0";
                    tsec = Long.parseLong(ts);
                } catch (Exception ignore) {
                }
                long result = ((((year * 365L + month * 30L + day) * 24L + hour) * 60L) + min) * 60L + sec;//) * 1000L + tsec;
                return result;
            }
        }
        return 0;
    }

    /**
     * Zerlegt Sekunden in Jahre, Monate, Tage und Uhrzeit mit 365 Tagen je Jahr und 30 Tagen je Monat.
     *
     * @param dauer Zeitdauer in Sekunden
     * @return Dauertext oder leerer String bei einer Dauer von null Sekunden
     */
    public static String toDauerString(long dauer) {
        long sec = dauer%60; dauer/=60;
        long min = dauer%60; dauer/=60;
        long hour= dauer%24; dauer/=24;
        long year= dauer/365;dauer%=365;
        long mont= dauer/30; dauer%=30;
        long day = dauer;
        String result="";
        if (year>0) result += year+"y";
        if (mont>0) result += (result.length()>0?" ":"")+mont+"m";
        if (day>0)  result += (result.length()>0?" ":"")+day+"d";
        if (hour>0 || min>0 || sec>0) result += (result.length()>0?" ":"")+hour+":"+min+":"+sec;
        return result;
    }

    /**
     * Bestimmt die Dauer in Sekunden zwischen zwei Datumswerten
     * @param datum1  erstes Datum
     * @param datum2  zweites Datum
     * @return        Sekunden datum2-datum1
     */
    public static long secondsBetweenDate(Date datum1, Date datum2) {
        long s1 = toDateInteger(datum1);
        long s2 = toDateInteger(datum2);
        return s2-s1;
    }

    /**
     * Bestimmt die Dauer in Sekunden von dem Datum date bis zur aktuellen Zeit
     *
     * @param date  Datum
     * @return      Dauer in Sekunden von date bis jetzt
     *
     */
    public static long secondsToNow(Date date) {
        long s1 = toDateInteger(date);
        long s2 = toDateInteger(LocalDateTime.now());
        return s2-s1;
    }

}
