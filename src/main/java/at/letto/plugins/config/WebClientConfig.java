package at.letto.plugins.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Stellt den HTTP-Client und dessen Builder als Spring-Beans bereit.
 */
@Configuration
public class WebClientConfig {

    /**
     * Erstellt einen Builder für HTTP-Clients.
     *
     * @return neuer WebClient-Builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // optional: vorkonfigurierter WebClient
    /**
     * Erstellt einen HTTP-Client mit dem übergebenen Builder.
     *
     * @param builder Builder für den HTTP-Client
     * @return konfigurierter HTTP-Client
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
