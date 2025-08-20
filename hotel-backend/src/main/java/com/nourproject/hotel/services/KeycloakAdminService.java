package com.nourproject.hotel.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.target-realm}")
    private String targetRealm;

    private Keycloak keycloak;
    private RealmResource realmResource;

    @PostConstruct
    public void initKeycloak() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
        
        this.realmResource = keycloak.realm(targetRealm);
    }


    public boolean updateUserCompleteProfile(String username, String firstName, String lastName) {
        try {
            // Step 1: Get users resource from Keycloak realm
            UsersResource usersResource = realmResource.users();
            
            // Step 2: Search for user by username (exact match)
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                System.err.println("User not found in Keycloak: " + username);
                return false;
            }
            
            // Step 3: Get the first user (should be unique by username)
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            
            // Step 4: Update basic profile information
            user.setFirstName(firstName);
            user.setLastName(lastName);
            UserResource userResource = usersResource.get(userId);
            userResource.update(user);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update complete user profile in Keycloak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




}
