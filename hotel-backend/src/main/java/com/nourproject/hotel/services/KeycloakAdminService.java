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

    /**
     * Updates user's first name, last name, and profile picture in Keycloak
     * This method handles complete profile synchronization from database to Keycloak
     * 
     * @param username The username to find the user in Keycloak
     * @param firstName The new first name
     * @param lastName The new last name
     * @param profilePicture The base64 encoded profile picture (can be null)
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserCompleteProfile(String username, String firstName, String lastName, String profilePicture) {
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
            
            // Step 5: Handle profile picture as custom attribute
            if (profilePicture != null && !profilePicture.isEmpty()) {
                // Initialize attributes map if null
                if (user.getAttributes() == null) {
                    user.setAttributes(new java.util.HashMap<>());
                }
                // Store profile picture as base64 string in Keycloak user attributes
                user.getAttributes().put("profilePicture", List.of(profilePicture));
                System.out.println("Profile picture added to Keycloak attributes for user: " + username);
            }
            
            // Step 6: Update the user in Keycloak
            UserResource userResource = usersResource.get(userId);
            userResource.update(user);
            
            System.out.println("Successfully updated complete user profile in Keycloak for: " + username);
            System.out.println("- First Name: " + firstName);
            System.out.println("- Last Name: " + lastName);
            System.out.println("- Profile Picture: " + (profilePicture != null ? "Updated" : "Not provided"));
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to update complete user profile in Keycloak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user's first name and last name only in Keycloak (for backward compatibility)
     * @param username The username to find the user
     * @param firstName The new first name
     * @param lastName The new last name
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserProfile(String username, String firstName, String lastName) {
        // Call the complete profile method without profile picture
        return updateUserCompleteProfile(username, firstName, lastName, null);
    }

    /**
     * Updates only the user's profile picture in Keycloak
     * @param username The username to find the user
     * @param profilePicture The base64 encoded profile picture
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserProfilePicture(String username, String profilePicture) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                System.err.println("User not found in Keycloak: " + username);
                return false;
            }
            
            UserRepresentation user = users.get(0);
            String userId = user.getId();
            
            // Initialize attributes if null
            if (user.getAttributes() == null) {
                user.setAttributes(new java.util.HashMap<>());
            }
            
            // Update only the profile picture attribute
            user.getAttributes().put("profilePicture", List.of(profilePicture));
            
            UserResource userResource = usersResource.get(userId);
            userResource.update(user);
            
            System.out.println("Successfully updated user profile picture in Keycloak for: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to update user profile picture in Keycloak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets user information from Keycloak including profile picture
     * This method retrieves complete user profile data for database synchronization
     * 
     * @param username The username to search for
     * @return UserRepresentation if found, null otherwise
     */
    public UserRepresentation getUserFromKeycloak(String username) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                System.out.println("Retrieved user from Keycloak: " + username);
                
                // Log if user has profile picture attribute
                if (user.getAttributes() != null && user.getAttributes().containsKey("profilePicture")) {
                    System.out.println("User has profile picture in Keycloak attributes");
                } else {
                    System.out.println("User has no profile picture in Keycloak attributes");
                }
                
                return user;
            }
            
        } catch (Exception e) {
            System.err.println("Failed to get user from Keycloak: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts profile picture from Keycloak user attributes
     * @param user The UserRepresentation from Keycloak
     * @return Base64 encoded profile picture or null if not found
     */
    public String getProfilePictureFromKeycloakUser(UserRepresentation user) {
        if (user != null && user.getAttributes() != null) {
            List<String> profilePictures = user.getAttributes().get("profilePicture");
            if (profilePictures != null && !profilePictures.isEmpty()) {
                return profilePictures.get(0);
            }
        }
        return null;
    }
}
