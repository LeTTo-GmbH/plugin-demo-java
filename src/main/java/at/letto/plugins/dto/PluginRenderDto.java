package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * Enthält gerenderten HTML- oder LaTeX-Code und die zugehörigen Bilder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRenderDto {

    /** LaTeX-Quelltext oder HTML-Quelltext */
    private String source ="";

    /** Liste der Bilder welche im Quelltext eingebunden werden. Sie werden im Verzeichnis img/ relativ zum
     * LaTeX-Quelltext positioniert. Müssen deshalb auch dementsprechend im LaTeX-Quelltext eingebunden sein.
     * Zulässig sind nur Bilder mit der Extension .png, .jpg, .jpeg, .pdf<br>
     * key   : Dateiname<br>
     * value : Base64 codiertes Bild<br>
     */
    private HashMap<String,String> images = new HashMap<>();

}
