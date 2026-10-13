package at.letto.plugins.tools;

import java.security.SecureRandom;

/**
 * statische Routine für das Erzeugen einer zufälligen Zeichenfolge für Passwort,Key,etc.
 */
public class RandomString {

    /**
     * Kleinbuchstaben des Zufallszeichenvorrats.
     */
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    /**
     * Großbuchstaben des Zufallszeichenvorrats.
     */
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    /**
     * Ziffern des Zufallszeichenvorrats.
     */
    private static final String NUMBER = "0123456789";
    /**
     * Zeichenvorrat aus Buchstaben und Ziffern.
     */
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    /**
     * Kryptografischer Zufallszahlengenerator für die Zeichenauswahl.
     */
    private static SecureRandom random = new SecureRandom();

    /**
     * Gibt den verfügbaren Zeichenvorrat und eine Beispielzeichenfolge auf der Konsole aus.
     *
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {

        System.out.println("String : " + DATA_FOR_RANDOM_STRING);
        System.out.println("result : " + generateRandomString(20));

    }

    /**
     * Erzeugt eine zufällige Zeichenfolge aus Großbuchstaben, Kleinbuchstaben und Ziffern.
     *
     * @param length Anzahl der Zeichen, mindestens 1
     * @return zufällige Zeichenfolge mit der angeforderten Länge
     * @throws IllegalArgumentException wenn die Länge kleiner als 1 ist
     */
    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            // System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);
        }

        return sb.toString();

    }

}
