package at.letto.plugins.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kopfdaten der Swagger-Dokumentation.<br>
 * Die Beschreibungen der Endpoints und DTOs kommen aus der JavaDoc (therapi-runtime-javadoc).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Titel, Version und Links zur JavaDoc und zur Hilfeseite des Plugin-Services.
     *
     * @return OpenAPI-Beschreibung, die springdoc um die Endpoints ergänzt
     */
    @Bean
    public OpenAPI pluginOpenApi() {
        String javadoc = Endpoint.open + "/javadoc/index.html";
        return new OpenAPI()
                .info(new Info()
                        .title(PluginConfiguration.PLUGIN_NAME)
                        .version(PluginConfiguration.PLUGIN_VERSION)
                        .description("REST-Schnittstelle des LeTTo-Plugin-Services. " +
                                "Die Beschreibungen der Endpoints und DTOs stammen aus der JavaDoc.<br>" +
                                "[JavaDoc](" + javadoc + ") · [Hilfeseite](" + Endpoint.servicepath + "/help)"))
                .externalDocs(new ExternalDocumentation()
                        .description("JavaDoc")
                        .url(javadoc));
    }

}
