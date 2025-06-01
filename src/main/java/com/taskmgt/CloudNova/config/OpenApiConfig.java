package com.taskmgt.CloudNova.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for CloudNova Task Management API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cloudNovaOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

//        Server prodServer = new Server();
//        prodServer.setUrl("https://api.cloudnova.com");
//        prodServer.setDescription("Production server");

//        Contact contact = new Contact();
//        contact.setEmail("support@cloudnova.com");
//        contact.setName("CloudNova Support Team");
//        contact.setUrl("https://www.cloudnova.com");

//        License mitLicense = new License()
//                .name("MIT License")
//                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("CloudNova Task Management API")
                .version("1.0.0")
//                .contact(contact)
                .description("A comprehensive REST API for managing users and tasks in the CloudNova platform. " +
                        "This API provides full CRUD operations for user management, task creation and tracking, " +
                        "advanced filtering, search capabilities, and statistical reporting.");
                //.termsOfService("https://www.cloudnova.com/terms")
//                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));//, prodServer));
    }
}