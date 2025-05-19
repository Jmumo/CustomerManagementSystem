package com.Jmumo.CardService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cards Service API")
                        .description("API for managing Customer accounts")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Cards Service  Api")
                                .email("jmn.mumo@gmail.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://Jmumo.com")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ));
    }
}
