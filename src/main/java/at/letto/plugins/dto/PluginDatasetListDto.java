package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Informationen über einen Dataset welcher vom Plugin angeforder werden kann
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginDatasetListDto {

    /**
     * Datensätze der Liste.
     */
    private List<PluginDatasetDto> datasets;

}
