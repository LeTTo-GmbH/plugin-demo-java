package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Daten die von einer SubQuestion an ein Plugin übergeben werden sollten
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginSubQuestionDto implements Cloneable {
    /** Name der SubQuestion */
    private String name;
    /** die erreichbare Punkteanzahl einer Teilfrage */
    private double points;
}
