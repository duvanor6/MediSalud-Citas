package com.medisalud.citas.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediSaludOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - MediSalud Citas")
                        .description("Sistema de agendamiento de citas médicas. Implementación basada en Arquitectura Hexagonal y DDD.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Duban Ortega Pacheco")
                                .email("duvanor6@gmail.com")));
    }
}