package com.marketplace.trade.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI listingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trade Service API")
                        .description("API documentation for the Card Marketplace Trade Service. "
                                + "This service manages trades, allowing users to create, "
                                + "update, and manage their trades.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Marketplace Team")
                                .email("support@marketplace.com")
                                .url("https://marketplace.com"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server"),
                        new Server()
                                .url("http://listing-service:" + serverPort)
                                .description("Docker Container Server")));
    }
}
