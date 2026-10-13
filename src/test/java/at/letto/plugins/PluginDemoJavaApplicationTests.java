package at.letto.plugins;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prüft, ob der Spring-Anwendungskontext des Plugin-Services startet.<br>
 * Die Pfade, die im Container per Umgebungsvariable gesetzt werden, zeigen im Test nach target/.
 * Die Setup-URI zeigt auf einen nicht erreichbaren Port, damit sich der Test nicht bei einer lokalen LeTTo-Installation registriert.
 * Ein echter Webserver auf zufälligem Port ist nötig, da PluginConfiguration den WebServerApplicationContext benötigt.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "letto.pathImages=target/test-storage/images",
        "letto.webPathImages=localhost/images",
        "letto.pathPlugins=target/test-storage/plugins",
        "letto.webPathPlugins=localhost/images/plugins",
        "letto.setup.uri=http://localhost:1"
})
class PluginDemoJavaApplicationTests {

    /**
     * Prüft durch Laden des Testkontexts, ob die Spring-Konfiguration initialisiert werden kann.
     */
    @Test
    void contextLoads() {

    }

}
