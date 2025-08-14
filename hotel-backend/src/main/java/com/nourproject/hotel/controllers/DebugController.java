package com.nourproject.hotel.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @GetMapping("/roles")
    public Map<String, Object> getRoles(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        if (authentication != null) {
            result.put("username", authentication.getName());
            
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            result.put("authorities", authorities != null ? authorities : "No authorities");
            
            Object principal = authentication.getPrincipal();
            result.put("principal", principal != null ? principal.toString() : "No principal");
            
            result.put("authenticated", authentication.isAuthenticated());
            
            // Debug JWT claims
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                result.put("jwt_claims", jwt.getClaims());
                result.put("realm_access", jwt.getClaimAsMap("realm_access"));
                result.put("preferred_username", jwt.getClaimAsString("preferred_username"));
                result.put("sub", jwt.getClaimAsString("sub"));
            }
            
        } else {
            result.put("error", "No authentication found");
        }
        
        return result;
    }
}
