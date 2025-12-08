package com.nourproject.hotel.controllers;

import com.nourproject.hotel.services.FaceRecognitionService;
import com.nourproject.hotel.services.KeycloakTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/face")  // Changed from public/face-auth to match frontend
@RequiredArgsConstructor
@Slf4j
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;
    private final KeycloakTokenService keycloakTokenService;

    /**
     * Register a user's face for face authentication
     * POST /api/face/register
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registerFace(
            @RequestParam("email") String email,
            @RequestParam("faceImage") MultipartFile faceImage) {
        try {
            // Validate file
            if (faceImage.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Please upload a valid image file");
                return ResponseEntity.badRequest().body(error);
            }

            // Validate file type
            String contentType = faceImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(error);
            }

            // Validate file size (max 5MB)
            if (faceImage.getSize() > 5 * 1024 * 1024) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Image size must be less than 5MB");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = faceRecognitionService.registerFaceByUsername(email, faceImage);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Face registration failed: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Unexpected error during face registration", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "An unexpected error occurred. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Authenticate user using face recognition
     * POST /public/face-auth/authenticate
     */
    @PostMapping(value = "/authenticate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> authenticateByFace(
            @RequestParam("faceImage") MultipartFile faceImage) {
        try {
            // Validate file
            if (faceImage.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Please upload a valid image file");
                return ResponseEntity.badRequest().body(error);
            }

            // Validate file type
            String contentType = faceImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Only image files are allowed");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = faceRecognitionService.authenticateByFace(faceImage);

            if ((Boolean) response.get("success")) {
                // Face authentication successful - generate Keycloak token
                String email = (String) response.get("email");
                String userName = (String) response.get("userName");
                
                if (email == null || email.isEmpty()) {
                    log.error("Email not found in face authentication response");
                    response.put("message", "Face authentication successful but user email not found");
                    return ResponseEntity.ok(response);
                }
                
                try {
                    Map<String, Object> tokenResponse = keycloakTokenService.generateTokenForFaceAuth(email);
                    
                    if (tokenResponse != null) {
                        // Include token information in the response
                        response.put("access_token", tokenResponse.get("access_token"));
                        response.put("token_type", tokenResponse.get("token_type"));
                        response.put("expires_in", tokenResponse.get("expires_in"));
                        response.put("refresh_token", tokenResponse.get("refresh_token"));
                        
                        // Mark if this is a fallback token
                        if (tokenResponse.containsKey("fallback") && (Boolean) tokenResponse.get("fallback")) {
                            response.put("tokenSource", "fallback");
                            log.warn("Using fallback token for face-authenticated user: {}", email);
                        } else {
                            response.put("tokenSource", "keycloak");
                            log.info("Successfully generated Keycloak token for face-authenticated user: {}", email);
                        }
                    } else {
                        log.error("Failed to generate token for user: {}", email);
                        response.put("message", "Face authentication successful but failed to generate authentication token");
                    }
                } catch (Exception e) {
                    log.error("Error generating token for face-authenticated user {}: {}", email, e.getMessage());
                    // Still return success for face authentication, but without token
                    response.put("message", "Face authentication successful but token generation failed");
                }
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (RuntimeException e) {
            log.error("Face authentication failed: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Unexpected error during face authentication", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "An unexpected error occurred. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Disable face authentication for a user
     * DELETE /public/face-auth/disable/{userId}
     */
    @DeleteMapping("/disable/{userId}")
    public ResponseEntity<Map<String, Object>> disableFaceAuth(@PathVariable Long userId) {
        try {
            Map<String, Object> response = faceRecognitionService.disableFaceAuth(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to disable face auth: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Unexpected error disabling face auth", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "An unexpected error occurred. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Check if user has face authentication enabled
     * GET /public/face-auth/status/{userId}
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getFaceAuthStatus(@PathVariable Long userId) {
        try {
            boolean enabled = faceRecognitionService.isFaceAuthEnabled(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("faceAuthEnabled", enabled);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking face auth status", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to check face authentication status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
