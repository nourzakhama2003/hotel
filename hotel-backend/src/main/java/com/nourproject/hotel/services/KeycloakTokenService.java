package com.nourproject.hotel.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakTokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak.admin.server-url:http://localhost:8080}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm:master}")
    private String adminRealm;

    @Value("${keycloak.admin.target-realm:hotelrealm}")
    private String targetRealm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    /**
     * Generate real Keycloak JWT token for a face-verified user
     * Uses admin impersonation to get a valid token
     */
    public Map<String, Object> generateTokenForFaceAuth(String email) {
        log.info("Generating Keycloak token for face-verified user: {}", email);
        
        try {
            // Step 1: Get admin token
            String adminToken = getAdminToken();
            
            // Step 2: Get user ID from Keycloak by email
            String userId = getUserId(email, adminToken);
            
            // Step 3: Impersonate user to get their token
            return impersonateUser(userId, adminToken);
            
        } catch (Exception e) {
            log.error("Failed to get Keycloak token: {}", e.getMessage(), e);
            return createFallbackToken(email);
        }
    }

    /**
     * Get admin access token from Keycloak
     */
    private String getAdminToken() {
        String tokenUrl = keycloakServerUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", adminClientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.postForEntity(tokenUrl, request, Map.class);
        
        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("access_token")) {
            throw new RuntimeException("Failed to get admin token");
        }
        
        log.debug("Successfully obtained admin token");
        return (String) body.get("access_token");
    }

    /**
     * Get Keycloak user ID by email
     */
    private String getUserId(String email, String adminToken) {
        String userUrl = keycloakServerUrl + "/admin/realms/" + targetRealm 
                       + "/users?email=" + email + "&exact=true";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        @SuppressWarnings("unchecked")
        ResponseEntity<List<Map<String, Object>>> response = (ResponseEntity<List<Map<String, Object>>>) (ResponseEntity<?>) restTemplate.exchange(userUrl, HttpMethod.GET, request, List.class);
        
        List<Map<String, Object>> users = response.getBody();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User not found in Keycloak: " + email);
        }
        
        String userId = (String) users.get(0).get("id");
        log.debug("Found user ID: {} for email: {}", userId, email);
        return userId;
    }

    /**
     * Impersonate user to get their real JWT token
     */
    private Map<String, Object> impersonateUser(String userId, String adminToken) {
        String impersonateUrl = keycloakServerUrl + "/admin/realms/" + targetRealm 
                              + "/users/" + userId + "/impersonation";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.postForEntity(impersonateUrl, request, Map.class);
        
        Map<String, Object> tokenResponse = response.getBody();
        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new RuntimeException("Failed to impersonate user");
        }
        
        log.info("Successfully generated real Keycloak JWT token via impersonation");
        return tokenResponse;
    }

    /**
     * Create a fallback token when Keycloak is unavailable
     * This is a temporary solution and should not be used in production
     */
    private Map<String, Object> createFallbackToken(String email) {
        log.warn("Using fallback token for user: {} (Keycloak unavailable)", email);
        return Map.of(
                "access_token", "FACE_AUTH_TOKEN_" + email + "_" + System.currentTimeMillis(),
                "token_type", "Bearer",
                "expires_in", 3600,
                "refresh_token", "FACE_AUTH_REFRESH_" + email,
                "scope", "openid profile email",
                "fallback", true
        );
    }
}
