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

    /**
     * Deletes a user from Keycloak using their email address
     * This method is called when a user is deleted from the local database
     * 
     * @param email The email address of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUserByEmail(String email) {
        try {
            // Step 1: Get users resource from Keycloak realm
            UsersResource usersResource = realmResource.users();
            
            // Step 2: Search for user by email (exact match)
            List<UserRepresentation> users = usersResource.searchByEmail(email, true);
            
            if (users.isEmpty()) {
                System.err.println("User not found in Keycloak with email: " + email);
                return false;
            }
            
            // Step 3: Get the first user (should be unique by email)
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            String username = user.getUsername();
            
            // Step 4: Delete the user from Keycloak
            UserResource userResource = usersResource.get(userId);
            userResource.remove();
            
            System.out.println("Successfully deleted user from Keycloak:");
            System.out.println("- Email: " + email);
            System.out.println("- Username: " + username);
            System.out.println("- User ID: " + userId);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to delete user from Keycloak with email: " + email);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a user from Keycloak using their username
     * Alternative method for deletion when you have username instead of email
     * 
     * @param username The username of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUserByUsername(String username) {
        try {
            // Step 1: Get users resource from Keycloak realm
            UsersResource usersResource = realmResource.users();
            
            // Step 2: Search for user by username (exact match)
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                System.err.println("User not found in Keycloak with username: " + username);
                return false;
            }
            
            // Step 3: Get the first user (should be unique by username)
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            String email = user.getEmail();
            
            // Step 4: Delete the user from Keycloak
            UserResource userResource = usersResource.get(userId);
            userResource.remove();
            
            System.out.println("Successfully deleted user from Keycloak:");
            System.out.println("- Username: " + username);
            System.out.println("- Email: " + email);
            System.out.println("- User ID: " + userId);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to delete user from Keycloak with username: " + username);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
