package com.example.be_service.service;

import com.example.be_service.config.KeycloakPropsConfig;

import com.example.be_service.dto.customer.CustomerAdminDTO;
import com.example.be_service.dto.customer.CustomerListDTO;
import com.example.be_service.dto.customer.CustomerProfileRequestDTO;
import com.example.be_service.dto.customer.CustomerDTO;
import com.example.be_service.dto.customer.GuestUserDTO;
import com.example.be_service.exception.NotFoundException;
import com.example.be_service.exception.WrongEmailFormatException;
import com.example.be_service.utils.Constants;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import org.apache.commons.validator.routines.EmailValidator;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final Keycloak keycloak;
    private final KeycloakPropsConfig keycloakPropsConfig;
    private static final String ERROR_FORMAT = "%s: Client %s don't have access right for this resource";
    private static final int USER_PER_PAGE = 2;
    private static final String GUEST = "GUEST";

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    public CustomerListDTO getCustomers(int pageNo) {
        try {
            List<CustomerAdminDTO> result = keycloak.realm(keycloakPropsConfig.getRealm()).users()
                    .search(null, pageNo * USER_PER_PAGE, USER_PER_PAGE).stream()
                    .map(CustomerAdminDTO::fromUserRepresentation)
                    .toList();
            int totalUser = keycloak.realm(keycloakPropsConfig.getRealm()).users().count();

            return new CustomerListDTO(totalUser, result, totalUser / USER_PER_PAGE);
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public void updateCustomers(CustomerProfileRequestDTO requestVm) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserRepresentation userRepresentation = keycloak.realm(keycloakPropsConfig.getRealm()).users().get(id).toRepresentation();
        if (userRepresentation != null) {
            RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
            UserResource userResource = realmResource.users().get(id);
            userRepresentation.setFirstName(requestVm.firstName());
            userRepresentation.setLastName(requestVm.lastName());
            userRepresentation.setEmail(requestVm.email());
            userResource.update(userRepresentation);
        } else {
            throw new NotFoundException(Constants.ERROR_CODE.USER_NOT_FOUND);
        }
    }

    public CustomerAdminDTO getCustomerByEmail(String email) {
        try {
            if (EmailValidator.getInstance().isValid(email)) {
                List<UserRepresentation> searchResult = keycloak.realm(keycloakPropsConfig.getRealm()).users().search(email, true);
                if (searchResult.isEmpty()) {
                    throw new NotFoundException(Constants.ERROR_CODE.USER_WITH_EMAIL_NOT_FOUND, email);
                }
                return CustomerAdminDTO.fromUserRepresentation(searchResult.getFirst());
            } else {
                throw new WrongEmailFormatException(Constants.ERROR_CODE.WRONG_EMAIL_FORMAT, email);
            }
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public CustomerDTO getCustomerProfile(String userId) {
        try {
            return CustomerDTO.fromUserRepresentation(keycloak.realm(keycloakPropsConfig.getRealm()).users().get(userId).toRepresentation());

        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public GuestUserDTO createGuestUser() {
            // Get realm
            RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
            String randomGuestName = generateSafeString();
            String guestUserEmail = randomGuestName + "_guest@yas.com";
            CredentialRepresentation credential = createPasswordCredentials(GUEST);

            // Define user
            UserRepresentation user = new UserRepresentation();
            user.setUsername(guestUserEmail);
            user.setFirstName(GUEST);
            user.setLastName(randomGuestName);
            user.setEmail(guestUserEmail);
            user.setCredentials(Collections.singletonList(credential));
            user.setEnabled(true);
            Response response = realmResource.users().create(user);

            // get new user
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource userResource = realmResource.users().get(userId);
            RoleRepresentation guestRealmRole = realmResource.roles().get(GUEST).toRepresentation();

            // Assign realm role GUEST to user
            userResource.roles().realmLevel().add(Collections.singletonList(guestRealmRole));

            return new GuestUserDTO(userId, guestUserEmail, GUEST);
    }

    public static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private String generateSafeString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
