package com.testcaju.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI configOpenApi() {
        return new OpenAPI().info(
                new Info().description("Implementação do autorizador de cartão de crédito.")
                        .title("Harley Souto Amaro Dalva - Entrevista Caju")
                        .version("1.0"));
    }

}