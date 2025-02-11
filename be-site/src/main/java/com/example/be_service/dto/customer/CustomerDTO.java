package com.example.be_service.dto.customer;

import org.keycloak.representations.idm.UserRepresentation;

public record CustomerDTO(String id, String username, String email, String firstName, String lastName) {
    public static CustomerDTO fromUserRepresentation(UserRepresentation userRepresentation) {
        return new CustomerDTO(userRepresentation.getId(), userRepresentation.getUsername(), userRepresentation.getEmail(),
                userRepresentation.getFirstName(), userRepresentation.getLastName());
    }
}
