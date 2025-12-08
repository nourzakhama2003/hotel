package com.nourproject.hotel.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class FaceAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        // Check if this is a face authentication token
        if (authHeader != null && authHeader.startsWith("Bearer FACE_AUTH_TOKEN_")) {
            try {
                String token = authHeader.substring(7); // Remove "Bearer " prefix
                
                // Extract username from token (format: FACE_AUTH_TOKEN_username_timestamp)
                String[] parts = token.split("_");
                if (parts.length >= 4) {
                    String username = parts[2]; // FACE_AUTH_TOKEN_username_timestamp
                    
                    log.debug("Processing face authentication token for user: {}", username);
                    
                    // Create authentication object with USER role
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    var authentication = new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            authorities
                    );
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Face authentication successful for user: {}", username);
                }
            } catch (Exception e) {
                log.error("Error processing face authentication token: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
