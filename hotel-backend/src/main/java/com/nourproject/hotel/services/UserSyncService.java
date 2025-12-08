package com.nourproject.hotel.services;

import com.nourproject.hotel.dtos.user.UserDto;
import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.enums.UserRole;
import com.nourproject.hotel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for syncing users between Keycloak and the database
 * This service runs at application startup to ensure all Keycloak users exist in the database
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService implements CommandLineRunner {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    /**
     * Runs at application startup to sync all Keycloak users to the database
     * Waits for Keycloak to be ready before attempting sync
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("Waiting for Keycloak to be ready before user synchronization...");
        
        // Wait up to 60 seconds for Keycloak to be ready
        int maxRetries = 12;  // 12 * 5 seconds = 60 seconds max wait
        int retryCount = 0;
        boolean keycloakReady = false;
        
        while (retryCount < maxRetries && !keycloakReady) {
            try {
                Thread.sleep(5000);  // Wait 5 seconds before each attempt
                log.info("Attempting to connect to Keycloak (attempt {}/{})", retryCount + 1, maxRetries);
                keycloakAdminService.getAllKeycloakUsers();  // Test connection
                keycloakReady = true;
                log.info("Keycloak is ready! Starting user synchronization...");
            } catch (Exception e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    log.warn("Keycloak not ready yet, retrying in 5 seconds... ({}/{})", retryCount, maxRetries);
                } else {
                    log.error("Keycloak not ready after {} attempts. Skipping user synchronization.", maxRetries);
                    return;
                }
            }
        }
        
        syncKeycloakUsersToDatabase();
        log.info("Keycloak to Database user synchronization completed.");
    }

    /**
     * Synchronizes all users from Keycloak to the database
     * Priority: Email first (more stable), then username as fallback
     */
    public void syncKeycloakUsersToDatabase() {
        try {
            // Get all users from Keycloak
            List<UserRepresentation> keycloakUsers = keycloakAdminService.getAllKeycloakUsers();
            
            if (keycloakUsers.isEmpty()) {
                log.warn("No users found in Keycloak or failed to retrieve users");
                return;
            }

            int syncedCount = 0;
            int skippedCount = 0;
            int errorCount = 0;

            for (UserRepresentation keycloakUser : keycloakUsers) {
                try {
                    boolean userSynced = syncSingleUser(keycloakUser);
                    if (userSynced) {
                        syncedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("Error syncing user {}: {}", 
                        keycloakUser.getUsername(), e.getMessage());
                }
            }

            log.info("User sync summary: {} synced, {} skipped, {} errors", 
                syncedCount, skippedCount, errorCount);

        } catch (Exception e) {
            log.error("Failed to sync Keycloak users to database: {}", e.getMessage());
        }
    }

    /**
     * Syncs a single user from Keycloak to the database
     * Uses email as primary and only identifier
     * 
     * @param keycloakUser The Keycloak user representation
     * @return true if user was synced/created, false if already exists
     */
    private boolean syncSingleUser(UserRepresentation keycloakUser) {
        String email = keycloakUser.getEmail();
        
        // Skip users without email
        if (email == null || email.trim().isEmpty()) {
            log.warn("Skipping user with no email: {} (username: {})", 
                keycloakUser.getId(), keycloakUser.getUsername());
            return false;
        }

        // Try to find by email only
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            log.debug("User already exists with email {}: {}", email, existingUser.getUserName());
            return false; // User already exists
        }

        // User doesn't exist, create new user
        return createUserFromKeycloak(keycloakUser);
    }

    /**
     * Creates a new user in the database from Keycloak user data
     * Email is used as the primary identifier
     * 
     * @param keycloakUser The Keycloak user representation
     * @return true if user was created successfully
     */
    private boolean createUserFromKeycloak(UserRepresentation keycloakUser) {
        try {
            String email = keycloakUser.getEmail();
            String username = keycloakUser.getUsername();
            
            // Ensure we have email (required)
            if (email == null || email.trim().isEmpty()) {
                log.warn("Cannot create user without email, username was: {}", username);
                return false;
            }

            // Use email as primary identifier, generate username if not available
            String finalEmail = email;
            String finalUsername = (username != null && !username.trim().isEmpty()) 
                ? username 
                : email.split("@")[0]; // Use email prefix as username if username is not available

            // Extract user role from Keycloak
            UserRole userRole = extractUserRole(keycloakUser);

            // Create UserDto for the service
            UserDto userDto = UserDto.builder()
                    .userName(finalUsername)
                    .email(finalEmail)
                    .firstName(keycloakUser.getFirstName())
                    .lastName(keycloakUser.getLastName())
                    .role(userRole)
                    .isActive(keycloakUser.isEnabled())
                    .build();

            // Create user via service
            userService.save(userDto);
            
            log.info("Created new user from Keycloak with email: {} (username: {})", finalEmail, finalUsername);
            return true;

        } catch (Exception e) {
            log.error("Failed to create user from Keycloak data: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts user role from Keycloak user representation
     * 
     * @param keycloakUser The Keycloak user
     * @return UserRole enum value
     */
    private UserRole extractUserRole(UserRepresentation keycloakUser) {
        try {
            // Get user roles from Keycloak using the admin service
            List<String> userRoles = keycloakAdminService.getUserRealmRoles(keycloakUser.getId());
            
            // Check if user has admin role
            if (userRoles.contains("admin")) {
                log.debug("User {} has admin role", keycloakUser.getEmail());
                return UserRole.Admin;
            }
            
            // Default to User role
            log.debug("User {} has user role (default)", keycloakUser.getEmail());
            return UserRole.User;
            
        } catch (Exception e) {
            log.warn("Failed to extract role from Keycloak user {}, defaulting to User role: {}", 
                keycloakUser.getEmail(), e.getMessage());
            return UserRole.User;
        }
    }

    /**
     * Manual sync method that can be called from controllers or other services
     * Useful for triggering sync without restarting the application
     */
    public void manualSync() {
        log.info("Manual user sync triggered");
        syncKeycloakUsersToDatabase();
    }
}
