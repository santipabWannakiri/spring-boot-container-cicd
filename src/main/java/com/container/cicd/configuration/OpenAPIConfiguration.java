package com.container.cicd.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {
    @Value("${jwt-auth.openapi.api.title}")
    private String title;

    @Value("${jwt-auth.openapi.api.version}")
    private String apiVersion;

    @Value("${jwt-auth.openapi.api.description}")
    private String description;

    @Value("${jwt-auth.openapi.url.dev}")
    private String devUrl;

    @Value("${jwt-auth.openapi.url.prod}")
    private String prodUrl;

    @Bean
    public OpenAPI openAPIConfgiure() {

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("This endpoint using for Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("This endpoint using for Production environment");

        Contact contact = new Contact();
        contact.setEmail("santipab1991@gmail.com");
        contact.setName("Nokia3310");
        contact.setUrl("https://www.demodemo.com");

        License license = new License().name("Demo License").url("https://demodemo.com/terms");

        Info info = new Info()
                .title(title)
                .version(apiVersion)
                .contact(contact)
                .description(description)
                .termsOfService("https://demodemo.com/terms")
                .license(license);

        return new OpenAPI().info(info).servers(List.of(devServer,prodServer));
    }
}
