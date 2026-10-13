package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

/**
 * VarHash für den Transport in Rest-Services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarHashDto implements Cloneable {

    /**
     * Variablen nach ihrem Namen in Einfügereihenfolge.
     */
    private LinkedHashMap<String,VarDto> vars;

    /**
     * Liest den Textwert einer Variablen anhand ihres Schlüssels.
     *
     * @return Textwert oder leerer String, wenn der Wert nicht gelesen werden kann
     * @param key Schlüssel der gesuchten Variablen
     */
    public String getString(String key) {
        try {
            VarDto varDto = vars.get(key);
            return varDto.getCalcErgebnisDto().getString();
        } catch (Exception ex) {}
        return "";
    }

    /**
     * Liest das Berechnungsergebnis einer Variablen anhand ihres Schlüssels.
     *
     * @return Berechnungsergebnis oder {@code null}, wenn der Wert nicht gelesen werden kann
     * @param key Schlüssel der gesuchten Variablen
     */
    public CalcErgebnisDto getCalcErgebnisDto(String key) {
        try {
            VarDto varDto = vars.get(key);
            return varDto.getCalcErgebnisDto();
        } catch (Exception ex) {}
        return null;
    }

    /**
     * Liefert eine Textdarstellung dieses Objekts.
     *
     * @return Textdarstellung des Objekts
     */
    @Override
    public String toString() {
        String ret = "";
        for (String name : vars.keySet()) {
            VarDto varDto = vars.get(name);
            ret += (ret.length()==0?"":",")+name+"="+varDto.toString();
        }
        return ret;
    }

}
