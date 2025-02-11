package com.example.be_service.dto.customer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.keycloak.representations.idm.UserRepresentation;

public record CustomerAdminDTO(String id, String username, String email, String firstName, String lastName,
                               LocalDateTime createdTimestamp) {
    public static CustomerAdminDTO fromUserRepresentation(UserRepresentation userRepresentation) {
        LocalDateTime createdTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), TimeZone.getDefault().toZoneId());
        return new CustomerAdminDTO(userRepresentation.getId(), userRepresentation.getUsername(),
                userRepresentation.getEmail(), userRepresentation.getFirstName(), userRepresentation.getLastName(), createdTimestamp);
    }
}
