package at.letto.plugins.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Kopfdaten der Swagger-Dokumentation.<br>
 * Die Beschreibungen der Endpoints und DTOs kommen aus der JavaDoc (therapi-runtime-javadoc).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Präfix der Anker, unter denen die Swagger-UI die Schemas im Abschnitt "Schemas" ablegt (nur bei OpenAPI 3.0).
     */
    private static final String SCHEMA_ANCHOR = "#model-";

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

    /**
     * Ergänzt bei Request body und Responses, die ein DTO verwenden, einen Link zum Schema des DTOs.
     *
     * @return Customizer, der nach dem Erzeugen der OpenAPI-Beschreibung ausgeführt wird
     */
    @Bean
    public OpenApiCustomizer schemaLinkCustomizer() {
        return openApi -> {
            keepReferenceDescriptions(openApi);
            if (openApi.getPaths() == null) return;
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                RequestBody requestBody = operation.getRequestBody();
                if (requestBody != null)
                    requestBody.setDescription(withSchemaLinks(requestBody.getDescription(), requestBody.getContent()));
                if (operation.getResponses() != null)
                    operation.getResponses().values().forEach(response ->
                            response.setDescription(withSchemaLinks(response.getDescription(), response.getContent())));
            }));
        };
    }

    /**
     * OpenAPI 3.0 erlaubt neben einem $ref keine Beschreibung, sie würde beim Ausgeben verworfen.<br>
     * Felder, die auf ein anderes DTO verweisen, werden daher als allOf mit dem Verweis und der Beschreibung ausgegeben.
     *
     * @param openApi OpenAPI-Beschreibung mit allen Schemas
     */
    private static void keepReferenceDescriptions(OpenAPI openApi) {
        if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) return;
        for (Schema<?> schema : openApi.getComponents().getSchemas().values()) {
            if (schema.getProperties() == null) continue;
            schema.getProperties().replaceAll((name, property) -> {
                if (property.get$ref() == null || property.getDescription() == null) return property;
                return new Schema<>()
                        .description(property.getDescription())
                        .allOf(List.of(new Schema<>().$ref(property.get$ref())));
            });
        }
    }

    /**
     * Hängt an eine Beschreibung die Links zu den verwendeten DTO-Schemas an.
     *
     * @param description bisherige Beschreibung, kann leer sein
     * @param content     Inhalt von Request body oder Response
     * @return Beschreibung mit Links, oder unverändert, wenn kein DTO verwendet wird
     */
    private static String withSchemaLinks(String description, Content content) {
        if (content == null) return description;
        // bereits ergänzt, falls der Customizer mehrfach läuft
        if (description != null && description.contains("](" + SCHEMA_ANCHOR)) return description;
        Set<String> names = new LinkedHashSet<>();
        for (MediaType mediaType : content.values())
            collectSchemaNames(mediaType.getSchema(), names);
        if (names.isEmpty()) return description;
        String links = "Schema: " + names.stream()
                .map(name -> "[" + name + "](" + SCHEMA_ANCHOR + name + ")")
                .collect(Collectors.joining(", "));
        return description == null || description.isBlank() ? links : description + "<br><br>" + links;
    }

    /**
     * Sammelt die Namen der referenzierten Schemas, auch bei Listen von DTOs.
     *
     * @param schema Schema des Inhalts
     * @param names  gefundene Schema-Namen
     */
    private static void collectSchemaNames(Schema<?> schema, Set<String> names) {
        if (schema == null) return;
        if (schema.get$ref() != null)
            names.add(schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1));
        else
            collectSchemaNames(schema.getItems(), names);
    }

}
