package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.services.UserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for administrative operations related to user synchronization
 * between Keycloak and the database
 */
@RestController
@RequestMapping("/api/admin/sync")  // Added /api for local dev, Nginx strips it in Docker
@RequiredArgsConstructor
public class UserSyncController {

    private final UserSyncService userSyncService;

    /**
     * Manually trigger synchronization of all Keycloak users to the database
     * This endpoint can be called to sync users without restarting the application
     * 
     * @return Response indicating sync completion
     */
    @PostMapping("/users")
    public ResponseEntity<Response> syncKeycloakUsers() {
        try {
            userSyncService.manualSync();
            
            return ResponseEntity.ok(
                Response.builder()
                    .status(200)
                    .message("User synchronization completed successfully")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Response.builder()
                    .status(500)
                    .message("User synchronization failed: " + e.getMessage())
                    .build()
            );
        }
    }
}
