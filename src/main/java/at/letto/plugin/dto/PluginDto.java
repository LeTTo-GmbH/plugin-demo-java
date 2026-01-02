package at.letto.plugin.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashMap;

/**
 * Dto welches vom Plugin über Letto zum Javascript des Plugins geschickt wird
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginDto {

    /** Url eines eingebetteten Bildes - meist base64 codiert */
    private String  imageUrl = "";

    /** True wenn das Plugin über ein PIG-Tag direkt in der Frage eingebunden ist */
    private boolean pig;

    /** True wenn Plugin in einer Subquestion definiert ist */
    private boolean result;

    /** Eindeutiger Bezeichner des PluginTags */
    private String  tagName = "";

    /** Breite des Plugin-Bereiches in Pixel */
    private int width = 500;

    /** Höhe des Plugin-Bereiches in Pixel */
    private int height = 500;

    /** Parameter welche vom Plugin an Javascript weitergegeben werden sollen,
     *  wird von LeTTo nicht verwendet */
    private HashMap<String,String> params = new HashMap<>();

    /** JSON-String welcher vom Plugin an Javascript weitergegeben werden soll,
     *  wird von LeTTo nicht verwendet */
    private String jsonData;

}
