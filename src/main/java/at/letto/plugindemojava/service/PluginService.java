package at.letto.plugindemojava.service;

import at.letto.plugindemojava.config.PluginConfiguration;
import at.letto.plugindemojava.dto.*;
import at.letto.plugindemojava.tools.Datum;
import at.letto.plugindemojava.tools.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@Service
public class PluginService implements PluginConnectionService {

    @Autowired private ApplicationContext  applicationContext;
    @Autowired private PluginConfiguration pluginConfiguration;
    @Autowired private ServerStatus serverStatus;

    private Logger logger = LoggerFactory.getLogger(PluginService.class);

    /** registriert das Plugin am Setup-Service */
    public void registerPlugin() {
        //RestSetupService setupService = lettoService.getSetupService();
        HashMap<String,String> params = new HashMap<>();

        ConfigServiceDto configServiceDto = new ConfigServiceDto(
                PluginConfiguration.PLUGIN_NAME,
                PluginConfiguration.PLUGIN_VERSION,
                PluginConfiguration.PLUGIN_AUTHOR,
                PluginConfiguration.PLUGIN_LICENSE,
                serverStatus.getBetriebssystem(),
                serverStatus.getIP(),
                serverStatus.getEncoding(),
                serverStatus.getJavaVersion(),
                "letto-pluginuhr",
                "letto-pluginuhr",
                pluginConfiguration.getUriIntern(),
                true,
                pluginConfiguration.getUriExtern(),
                true,
                false,
                true,
                pluginConfiguration.getUserUserName(),
                pluginConfiguration.getUserUserPassword(),
                false,
                Datum.toDateInteger(new Date(applicationContext.getStartupDate())),
                Datum.nowDateInteger(),
                params
        );
        String setupUri = pluginConfiguration.getSetupServiceUri();
        //hier kommt die REST-Anfrage an den Server
        RegisterServiceResultDto result=null;
        try {
            result = pluginConfiguration.getWebClientSetupUser()
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

    /**
     * @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden
     */
    @Override
    public List<String> getPluginList() {
        return List.of();
    }

    /**
     * @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services
     */
    @Override
    public List<PluginGeneralInfo> getPluginGeneralInfoList() {
        return List.of();
    }

    /**
     * @param typ Plugin Typ
     * @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @Override
    public PluginGeneralInfo getPluginGeneralInfo(String typ) {
        return null;
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Antworterzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return HTML Text
     */
    @Override
    public String getHTML(String typ, String name, String config, String params, PluginQuestionDto q) {
        return "";
    }

    /**
     * Liefert einen Angabestring für die MoodleText Angabe
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Einstellungen
     * @return String für das MoodleText-Feld
     */
    @Override
    public String getAngabe(String typ, String name, String config, String params) {
        return "";
    }

    /**
     * Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liste der Datensatzdefinitionen welche vom Plugin in der Frage angefordert werden
     */
    @Override
    public List<PluginDatasetDto> generateDatasets(String typ, String name, String config) {
        return List.of();
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     *
     * @param typ                  Typ des Plugins
     * @param name                 Name des Plugins in der Frage
     * @param config               Konfigurationsstring des Plugins
     * @param params               Parameter
     * @param q                    Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return Maxima Berechnungs-String
     */
    @Override
    public String getMaxima(String typ, String name, String config, String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode) {
        return "";
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild
     */
    @Override
    public ImageBase64Dto getImage(String typ, String name, String config, String params, PluginQuestionDto q) {
        return null;
    }

    /**
     * Liefert eine Url auf ein Bild mit den angegebenen Parametern
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Url auf das erstellte Bild
     */
    @Override
    public ImageUrlDto getImageUrl(String typ, String name, String config, String params, PluginQuestionDto q) {
        return null;
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liefert eine Liste aller möglichen Varianten von Bildern
     */
    @Override
    public Vector<String[]> getImageTemplates(String typ, String name, String config) {
        return null;
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param vars   Alle Variablen der Frage
     * @param cp     Berechnungsparameter
     * @param p      Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return Ergebnis der Funktion
     */
    @Override
    public CalcErgebnisDto parserPlugin(String typ, String name, String config, VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto... p) {
        return null;
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param p      Einheiten der Parameter als Recheneinheiten
     * @return Recheneinheit des Ergebnisses
     */
    @Override
    public String parserPluginEinheit(String typ, String name, String config, String... p) {
        return "";
    }

    /**
     * Prüft die Eingabe eines Schülers
     *
     * @param typ          Typ des Plugins
     * @param name         Name des Plugins in der Frage
     * @param config       Konfigurationsstring des Plugins
     * @param pluginDto    PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort      Antwort die der Schüler eingegeben hat
     * @param toleranz     Toleranz für die Lösung
     * @param varsQuestion Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto    Antwort des Schülers
     * @param grade        Maximale Punktanzahl für die richtige Antwort
     * @return Bewertung
     */
    @Override
    public PluginScoreInfoDto score(String typ, String name, String config, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade) {
        return null;
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liste aller Variablen des Plugins
     */
    @Override
    public Vector<String> getVars(String typ, String name, String config) {
        return null;
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text   Text der innerhalb der PI Tags gestanden ist
     * @param q      Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return veränderter Text
     */
    @Override
    public String modifyAngabe(String typ, String name, String config, String text, PluginQuestionDto q) {
        return "";
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text   Angabetext der Frage
     * @param q      Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return veränderter AngabeText
     */
    @Override
    public String modifyAngabeTextkomplett(String typ, String name, String config, String text, PluginQuestionDto q) {
        return "";
    }

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     *
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDef akt. Plugin-Definition
     * @param jsResult  Rückgabe von Javascript
     * @return    aktualiesierte Plugindefinition
     */
    @Override
    public String updatePluginstringJavascript(String typ, String name, String config, String pluginDef, String jsResult) {
        return "";
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Plugin-Parameter
     * @param q      Question, in die das Plugin eingebettet ist
     * @param nr     Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return PluginDto
     */
    @Override
    public PluginDto loadPluginDto(String typ, String name, String config, String params, PluginQuestionDto q, int nr) {
        return null;
    }

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     *
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDto Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return Latexsourcode und zugehörige Bilder in einer Hashmap
     */
    @Override
    public PluginRenderDto renderLatex(String typ, String name, String config, PluginDto pluginDto, String answer, String mode) {
        return null;
    }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     *
     * @param typ          Typ des Plugins
     * @param name         Name des Plugins in der Frage
     * @param config       Konfigurationsstring des Plugins
     * @param tex          true für LaTeX-Code, false für html-Code
     * @param pluginDto    PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort      Antwort die der Schüler eingegeben hat
     * @param toleranz     Toleranz für die Lösung
     * @param varsQuestion Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto    Antwort des Schülers
     * @param grade        Maximale Punktanzahl für die richtige Antwort
     * @return HTML-Code oder LaTeX-Code mit Bildern
     */
    @Override
    public PluginRenderDto renderPluginResult(String typ, String name, String config, boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade) {
        return null;
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     *
     * @param typ             Typ des Plugins
     * @param name            Name des Plugins in der Frage
     * @param config          Konfigurationsstring des Plugins
     * @param configurationID eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @param timeout         maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen
     * @return alle notwendigen Konfig
     */
    @Override
    public PluginConfigurationInfoDto configurationInfo(String typ, String name, String config, String configurationID, long timeout) {
        return null;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     *
     * @param typ             Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return Liefert die Daten welche an JS weitergeleitet werden.
     */
    @Override
    public PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto) {
        return null;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     *
     * @param typ             Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID
     * @return Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Override
    public String getConfiguration(String typ, String configurationID) {
        return "";
    }
}
