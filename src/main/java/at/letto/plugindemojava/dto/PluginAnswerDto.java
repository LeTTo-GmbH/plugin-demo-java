package at.letto.plugindemojava.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * korrekte Antwort und Informationen für den Scorer des Plugins
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginAnswerDto {
    /** korrektes Ergebnis */
    CalcErgebnisDto ergebnis;
    /** korrekte Antwort aus der Frage als String */
    String          answerText;
    /** Zieleinheit für die Korrektur */
    String          ze;
}
