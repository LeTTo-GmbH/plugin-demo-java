package at.letto.plugins.dto;

import at.letto.plugins.enums.Score;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ScoreInfoDto für die score-Methode von Plugins
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginScoreInfoDto {

    /** Eingabe des Schülers geparst als CalcErgebnisDto */
    CalcErgebnisDto schuelerErgebnis;
    /** ZielEinheit der Korrektur als String */
    String zielEinheit;
    /** Punkte IST von der Korrektur */
    double punkteIst;
    /** Punkte SOLL erreichbar */
    double punkteSoll;
    /** Score Ergebnis als Enum-Feld */
    Score status;
    /* HTML Score Information */
    String htmlScoreInfo;
    /** Feedback */
    String feedback;

}
