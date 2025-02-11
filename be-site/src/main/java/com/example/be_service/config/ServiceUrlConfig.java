package com.example.be_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(
        String location) {
}
