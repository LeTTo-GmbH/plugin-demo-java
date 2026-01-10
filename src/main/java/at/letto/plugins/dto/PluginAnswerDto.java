package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * korrekte Antwort und Informationen für den Scorer des Plugins
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginAnswerDto {
    /** korrektes Ergebnis */
    CalcErgebnisDto ergebnis;
    /** korrekte Antwort aus der Frage als String */
    String          answerText;
    /** Zieleinheit für die Korrektur */
    String          ze;
}
