package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Liefert allgemein Informationen zu einem Plugin, welche ohne Definition einer Plugin-Instanz allgemein gültig sind
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PluginGeneralInfoList {

    private List<PluginGeneralInfo> pluginInfos;

}
