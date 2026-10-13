package at.letto.plugins.dto;

import at.letto.plugins.plugin.PluginService;
import at.letto.plugins.tools.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dto welches vom Plugin über Letto zum JavaScript des Plugins geschickt wird
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    /** Parameter welche vom Plugin an JavaScript weitergegeben werden sollen,
     *  wird von LeTTo nicht verwendet */
    private HashMap<String,String> params = new HashMap<>();

    /** JSON-String welcher vom Plugin an JavaScript weitergegeben werden soll,
     *  wird von LeTTo nicht verwendet */
    private String jsonData;

    /**
     * Erstellt Darstellungsdaten mit eindeutigem Tag-Bezeichner und Abmessungen.
     *
     * @param params Parameterstring des Plugins
     * @param pi Plugin-Instanz
     * @param q Frage, in die das Plugin eingebettet ist
     * @param nr laufende Nummer des Plugins innerhalb der Frage
     */
    public PluginDto(String params, PluginService pi, PluginQuestionDto q, int nr) {
        setSize(params);
        tagName = q.getId() + "_" + pi.getName() + "_" + nr;
        String src="";
        try {
            ImageUrlDto imageUrlDto = null;
            try {
                imageUrlDto = pi.getImageUrl(params, q);
            } catch (Exception ignore) {}
            if (imageUrlDto != null && imageUrlDto.getImageUrl() != null && imageUrlDto.getImageUrl().trim().length() > 0) {
                src = imageUrlDto.getImageUrl();
            } else {
                ImageBase64Dto imageBase64Dto = pi.getImageDto(params, q);
                src = "data:image/png;base64," + imageBase64Dto.getBase64Image();
            }
            setImageUrl(src);
        } catch (Exception ex) {}
        this.width = pi.getWidth();
        this.height = pi.getHeight();
    }

    /**
     * Serialisiert dieses DTO als JSON.
     *
     * @return JSON-Darstellung des DTOs
     */
    public String toJson() {
        return JSON.objToJson(this);
    }

    /**
     * Erstellt Darstellungsdaten mit eindeutigem Tag-Bezeichner und Abmessungen.
     *
     * @param params Parameterstring des Plugins
     * @param name Name beziehungsweise Bezeichner
     * @param nr laufende Nummer des Plugins innerhalb der Frage
     * @param idQuestion ID der Frage
     */
    public PluginDto(String params, String name, int nr, long idQuestion) {
        setSize(params);
        tagName = idQuestion + "_" + name + "_" + nr;
    }

    /**
     * Übernimmt {@code size=N} oder {@code size=BreitexHöhe} aus den kommaseparierten Parametern.
     *
     * @param params Parameterstring des Plugins
     */
    @JsonIgnore
    private void setSize(String params) {
        for (String p:params.split(",")) {
            Matcher m;
            if ((m= Pattern.compile("^size=(\\d+)x(\\d+)$").matcher(p)).find()) {
                int w = Integer.parseInt(m.group(1));
                int h = Integer.parseInt(m.group(2));
                width = w;
                height= h;
            } else if ((m=Pattern.compile("^size=(\\d+)$").matcher(p)).find()) {
                int w = Integer.parseInt(m.group(1));
                width = w;
                height= w;
            }
        }
    }

}
