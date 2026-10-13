package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Überträgt allgemeine Service-Informationen und Angaben zur Laufzeitumgebung.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfoDTO {

    /** Name des Services */
    protected String serviceName="";
    /** Version des Services */
    protected String version="";
    /** Information über den Autor des Services */
    protected String author="";
    /** Information über die Lizenz des Services */
    protected String license="";
    /** Information über die Endpoints des Services */
    protected String endpoints="";
    /** Jardatei welche gestartet wurde */
    protected String jarfilename="";
    /** Datum und Uhrzeit des Service-Starts */
    protected String starttime="";
    /** Administrative Information über das Service */
    protected AdminInfoDto adminInfoDto=null;
    /** Liste aller Library-jars welche in diesem Service verwendet werden */
    protected List<String> jarLibs=new ArrayList<>();

}
