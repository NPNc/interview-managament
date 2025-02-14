package com.example.be_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "keycloak")
public class KeycloakPropsConfig {

    private String authServerUrl;
    private String realm;
    private String resource;
    private Credentials credentials = new Credentials();

    @Getter
    @Setter
    public static class Credentials {

        private String secret;
    }
}