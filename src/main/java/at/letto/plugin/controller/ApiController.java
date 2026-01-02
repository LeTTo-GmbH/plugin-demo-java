package at.letto.plugin.controller;

import at.letto.plugin.config.Endpoint;
import at.letto.plugin.config.PluginConfiguration;
import at.letto.plugin.dto.*;
import at.letto.plugin.plugins.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Vector;

/**
 * REST-Schnittstelle des Plugins zwischen LeTTo und Plugin (nur aus dem Docker-Netzwerk erreichbar)
 */
@RestController
@RequestMapping(Endpoint.LOCAL_API)
@Tag(name = "Api Controller",
        description = "REST-Schnittstelle des Plugins zwischen LeTTo und Plugin (nur aus dem Docker-Netzwerk erreichbar) " +
                "[JavaDoc](https://build.letto.at/pluginname/open/javadoc/at/letto/plugindemojava/controller/ApiController.html)"
)
public class ApiController {

    @Autowired PluginConfiguration pluginConfiguration;
    
    /** @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden */
    @Operation(summary = "liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden")
    @GetMapping(Endpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        List<String> pluginList = pluginConfiguration.getPluginList();
        return ResponseEntity.ok(pluginList);
    }

    /** @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services*/
    @GetMapping(Endpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        List<PluginGeneralInfo> resultList = pluginConfiguration.getPluginGeneralInfoList();
        PluginGeneralInfoList result = new PluginGeneralInfoList(resultList);
        return ResponseEntity.ok(result);
    }

    /**
     * @param plugintyp Typ des Plugins (z.B. Wsr) mit dem das Plugin auch in LeTTo angesprochen wird
     * @return          liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @PostMapping(Endpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(
            @RequestBody String plugintyp) {
        PluginGeneralInfo result = pluginConfiguration.getPluginGeneralInfo(plugintyp);
        return ResponseEntity.ok(result);
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     HTML Text des Plugins
     */
    @PostMapping(Endpoint.getHTML)
    public ResponseEntity<String> getHtml(
            @RequestBody PluginRequestDto r) {
        //String result = pluginService.getHTML(r.getTyp(),r.getName(),r.getConfig(),r.getParams(),r.getQ());
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getHTML(r.getParams(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert einen Angabestring für die Text-Angabe
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     * @return     Angabetext für das Textfeld der Frage
     */
    @PostMapping(Endpoint.getAngabe)
    public ResponseEntity<String> getAngabe(@RequestBody PluginRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getAngabe(r.getParams());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return     Liste der Datensatzdefinitionen welche vom Plugin in der Frage angefordert werden
     */
    @PostMapping(Endpoint.generateDatasets)
    public ResponseEntity<PluginDatasetListDto> generateDatasets(@RequestBody PluginRequestDto r) {
        PluginDatasetListDto result = new PluginDatasetListDto(
                pluginConfiguration
                        .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                        .generateDatasets()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     *   r.pluginMaximaCalcMode PluginMaximaCalcModeDto: Berechnungsmode der Frage <br>
     * @return     Maxima Berechnungs-String
     */
    @PostMapping(Endpoint.getMaxima)
    public ResponseEntity<String> getMaxima(@RequestBody PluginRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getMaxima(r.getParams(),r.getQ(),r.getPluginMaximaCalcMode());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     Base64 kodiertes Bild
     */
    @PostMapping(Endpoint.getImage)
    public ResponseEntity<ImageBase64Dto> getImage(@RequestBody PluginRequestDto r) {
        ImageBase64Dto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getImageDto(r.getParams(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays
     *      Element 0 : beschreibender Text
     *      Element 1 : PIG Tag
     *      Element 2 : Hilfetext
     */
    @PostMapping(Endpoint.getImageTemplates)
    public ResponseEntity<Vector<String[]>> getImageTemplates(@RequestBody PluginRequestDto r) {
        Vector<String[]> result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getImageTemplates();
        return ResponseEntity.ok(result);
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     * @param r    PluginParserRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.vars    VarHashDto: Alle Variablen der Frage<br>
     *   r.cp      CalcParamsDto: Berechnungsparameter<br>
     *   r.p       CalcErgebnisDto[]: Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return     Ergebnis der Funktion als CalcErgebnisDto
     */
    @PostMapping(Endpoint.parserPlugin)
    public ResponseEntity<CalcErgebnisDto> parserPlugin(@RequestBody PluginParserRequestDto r) {
        CalcErgebnisDto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .parserPlugin(r.getVars(),r.getCp(),r.getP());
        return ResponseEntity.ok(result);
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     * @param r    PluginEinheitRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.p       String[]: Einheiten der Parameter als Recheneinheiten
     * @return     Recheneinheit des Ergebnisses
     */
    @PostMapping(Endpoint.parserPluginEinheit)
    public ResponseEntity<String> parserPluginEinheit(@RequestBody PluginEinheitRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .parserPluginEinheit(r.getP());
        return ResponseEntity.ok(result);
    }

    /**
     * Prüft die Eingabe eines Schülers
     * @param r    PluginScoreRequestDto:<br>
     *   r.typ          String: Typ des Plugins<br>
     *   r.name         String: Name des Plugins in der Frage<br>
     *   r.config       String: Konfigurationsstring des Plugins<br>
     *   r.pluginDto    PluginDto: PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde<br>
     *   r.antwort      String: Antwort die der Schüler eingegeben hat<br>
     *   r.toleranz     ToleranzDto: Toleranz für die Lösung<br>
     *   r.varsQuestion VarHashDto: VarHash aller Variablen der Frage<br>
     *   r.answerDto    PluginAnswerDto: korrekte Antwort und Informationen für den Scorer des Plugins<br>
     *   r.grade        double: Maximale Punktanzahl für die richtige Antwort<br>
     * @return     PluginScoreInfoDto: Bewertung
     */
    @PostMapping(Endpoint.score)
    public ResponseEntity<PluginScoreInfoDto> score(@RequestBody PluginScoreRequestDto r) {
        PluginScoreInfoDto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .score(r.getPluginDto(),r.getAntwort(),r.getToleranz(),r.getVarsQuestion(),r.getAnswerDto(), r.getGrade());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return     Liste aller Variablen des Plugins
     */
    @PostMapping(Endpoint.getVars)
    public ResponseEntity<Vector<String>> getVars(@RequestBody PluginRequestDto r) {
        Vector<String> result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .getVars();
        return ResponseEntity.ok(result);
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param r    PluginAngabeRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.text    String: Text der innerhalb der PI Tags gestanden ist<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     veränderter Text
     */
    @PostMapping(Endpoint.modifyAngabe)
    public ResponseEntity<String> modifyAngabe(@RequestBody PluginAngabeRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .modifyAngabe(r.getText(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param r    PluginAngabeRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.text    String: Angabetext der Frage<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     veränderter AngabeText
     */
    @PostMapping(Endpoint.modifyAngabeTextkomplett)
    public ResponseEntity<String> modifyAngabeTextkomplett(@RequestBody PluginAngabeRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .modifyAngabeTextkomplett(r.getText(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     *Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param r    PluginUpdateJavascriptRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.pluginDef String: akt. Plugin-Definition<br>
     *   r.jsResult  String: Rückgabe von Javascript<br>
     * @return     aktualiesierte Plugindefinition
     */
    @PostMapping(Endpoint.updatePluginstringJavascript)
    public ResponseEntity<String> updatePluginstringJavascript(@RequestBody PluginUpdateJavascriptRequestDto r) {
        String result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .updatePluginstringJavascript(r.getPluginDef(),r.getJsResult());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param r    LoadPluginRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.params    String: Plugin-Parameter<br>
     *   r.q         PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     *   r.nr        int: Laufende Nummer für alle PIG-Tags und Question-Plugins<br>
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @PostMapping(Endpoint.loadPluginDto)
    public ResponseEntity<PluginDto> loadPluginDto(@RequestBody LoadPluginRequestDto r) {
        PluginDto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .loadPluginDto(r.getParams(),r.getQ(),r.getNr());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param r    PluginRenderLatexRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.pluginDto PluginDto: Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>
     *   r.answer    String: Inhalt des Antwortfeldes welches der Schüler eingegeben hat<br>
     *   r.mode      String: Druckmode<br>
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @PostMapping(Endpoint.renderLatex)
    public ResponseEntity<PluginRenderDto> renderLatex(@RequestBody PluginRenderLatexRequestDto r) {
        PluginRenderDto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .renderLatex(r.getPluginDto(),r.getAnswer(),r.getMode());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param r    PluginRenderResultRequestDto:<br>
     *   r.typ          String: Typ des Plugins<br>
     *   r.name         String: Name des Plugins in der Frage<br>
     *   r.config       String: Konfigurationsstring des Plugins<br>
     *   r.tex          boolean: true für LaTeX-Code, false für html-Code<br>
     *   r.pluginDto    PluginDto: Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>
     *   r.antwort      String: Antwort die der Schüler eingegeben hat<br>
     *   r.toleranz     ToleranzDto: Toleranz für die Lösung<br>
     *   r.varsQuestion VarHashDto: VarHash aller Variablen der Frage<br>
     *   r.answerDto    PluginAnswerDto: korrekte Antwort und Informationen für den Scorer des Plugins<br>
     *   r.grade        double: Maximale Punktanzahl für die richtige Antwort<br>
     * @return     HTML-Code oder LaTeX-Code mit Bildern
     */
    @PostMapping(Endpoint.renderPluginResult)
    public ResponseEntity<PluginRenderDto> renderPluginResult(@RequestBody PluginRenderResultRequestDto r) {
        PluginRenderDto result = pluginConfiguration
                .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                .renderPluginResult(r.isTex(),r.getPluginDto(),r.getAntwort(),r.getToleranz(),r.getVarsQuestion(),r.getAnswerDto(),r.getGrade());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param r    PluginConfigurationInfoRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.configurationID String:eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird<br>
     *   r.timeout   long:maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen<br>
     * @return     alle notwendigen Konfigurationen
     */
    @PostMapping(Endpoint.configurationInfo)
    //public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
    public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
        String configurationID = r.getConfigurationID();
        String typ             = r.getTyp();
        PluginConfigurationInfoDto result = pluginConfiguration.configurationInfo(r.getTyp(),r.getName(),r.getConfig(),r.getConfigurationID(),r.getTimeout());
        result.setConfigurationUrl(pluginConfiguration.getBaseUriExtern()+Endpoint.iframeConfig+
                "?typ="+typ+"&configurationID="+configurationID);
        return ResponseEntity.ok(result);
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param r    PluginSetConfigurationDataRequestDto:<br>
     *   r.configurationID String:zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)<br>
     *   r.configuration   String:aktueller Konfigurations-String des Plugins<br>
     *   r.questionDto     PluginQuestionDto: Question-DTO mit Varhashes<br>
     * @return     Fehlermeldung wenn etwas nicht korrekt funktioniert hat
     */
    @PostMapping(Endpoint.setConfigurationData)
    public ResponseEntity<PluginConfigDto> setConfigurationData(@RequestBody PluginSetConfigurationDataRequestDto r) {
        PluginConfigDto pluginConfigDto = pluginConfiguration.setConfigurationData(r.getTyp(), r.getConfigurationID(),r.getConfiguration(),r.getQuestionDto());
        pluginConfigDto.setPluginDtoUri(pluginConfiguration.getUriGetPluginDto());
        pluginConfigDto.setPluginDtoToken("");
        return ResponseEntity.ok(pluginConfigDto);
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param      dto    typ und Configuration-ID<br>
     *   r.typ              String: PluginTyp<br>
     *   r.configurationID  String:zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)<br>
     * @return     Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @PostMapping(Endpoint.getConfiguration)
    public ResponseEntity<String> getConfiguration(@RequestBody PluginConfigurationRequestDto dto) {
        String result = pluginConfiguration.getConfiguration(dto.getTyp(), dto.getConfigurationID());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param r    LoadPluginRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.params    String: Plugin-Parameter<br>
     *   r.nr        int: Laufende Nummer für alle PIG-Tags und Question-Plugins<br>
     *   r.configurationID String:ID der aktuellen Konfiguration
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @PostMapping(Endpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        String configurationID = r.getConfigurationID();
        String typ = r.getTyp();
        // Suche nun eine bestehende Verbindung mit der confingurationID
        PluginConfigurationConnection conn = pluginConfiguration.getConfigurationConnection(typ, configurationID);
        if (conn != null) {
            // Lade das Plugin mit der Konfiguration aus den Request-Parametern
            PluginService pluginService = pluginConfiguration.createPluginService(r.getTyp(),conn.getName(),r.getConfig());
            // aktualisiere die Verbindung, damit dann LeTTo die Konfiguration wieder korrekt abrufen kann
            conn.changeConfig(r.getConfig(), pluginService);
            // erzeuge das PluginDto welches dann zum Rendern des Plugins verwendet wird
            PluginDto result = pluginConfiguration
                    .createPluginService(r.getTyp(),r.getName(),r.getConfig())
                    .loadPluginDto(r.getParams(),conn.pluginQuestionDto,r.getNr());
            return ResponseEntity.ok(result);
        }
        return null;
    }

}
