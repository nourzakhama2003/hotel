package com.nourproject.hotel.services;
import com.nourproject.hotel.dtos.UserDto;
import com.nourproject.hotel.dtos.UserUpdateDto;
import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.mappers.UserMapper;
import com.nourproject.hotel.repositories.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    KeycloakAdminService keycloakAdminService;

    @PersistenceContext
    private EntityManager entityManager;

    public UserService(UserMapper userMapper, UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    public List<User> findAll(){
        return this.userRepository.findAll();
    }

    public User findById(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
    }

    public User save(UserDto userDto ){
        return this.userRepository.save(this.userMapper.userDtoToUser(userDto));
    }

    public User findByUserName(String userName) {
        return this.userRepository.findByUserName(userName).orElse(null);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public User updateByUsername(String username, UserUpdateDto userUpdateDto) {
        User user = this.userRepository.findByUserName(username)
                .orElseThrow(() -> new GlobalException("User with username " + username + " not found"));
        
        // Use updateById which now includes Keycloak sync
        return updateById(user.getId(), userUpdateDto);
    }

    public User syncFromKeycloak(UserDto keycloakData) {
        System.out.println("SYNC FROM KEYCLOAK CALLED - This might be overriding your database changes!");
        System.out.println("Keycloak data: " + keycloakData);
        
        User existingUser = findByUserName(keycloakData.getUserName());

        if (existingUser != null) {
            System.out.println("Existing user found, updating from Keycloak data (THIS MIGHT OVERRIDE YOUR CHANGES)");
            UserUpdateDto updateDto = userMapper.userDtoToUserUpdateDto(keycloakData);
            return updateById(existingUser.getId(), updateDto);
        } else {
            System.out.println("Creating new user from Keycloak data");
            return save(keycloakData);
        }
    }

    public User updateById(Long id, UserUpdateDto userUpdateDto){
        System.out.println("=== UPDATE BY ID CALLED ===");
        System.out.println("User ID: " + id);
        System.out.println("Update data: " + userUpdateDto);
        
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
                
        System.out.println("BEFORE UPDATE - User in database:");
        System.out.println("- FirstName: " + user.getFirstName());
        System.out.println("- LastName: " + user.getLastName());
        System.out.println("- ProfileImage: " + (user.getProfileImage() != null ? "Has image" : "No image"));
        
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
        
        System.out.println("AFTER MAPPING - User object:");
        System.out.println("- FirstName: " + user.getFirstName());
        System.out.println("- LastName: " + user.getLastName());
        System.out.println("- ProfileImage: " + (user.getProfileImage() != null ? "Has image" : "No image"));
        
        // Save to database first
        User updatedUser = this.userRepository.save(user);
        
        System.out.println("AFTER DATABASE SAVE - User object:");
        System.out.println("- FirstName: " + updatedUser.getFirstName());
        System.out.println("- LastName: " + updatedUser.getLastName());
        System.out.println("- ProfileImage: " + (updatedUser.getProfileImage() != null ? "Has image" : "No image"));
        
        // Then sync to Keycloak - this is the simplest way as requested
        try {
            System.out.println("Syncing user profile to Keycloak for: " + updatedUser.getUserName());
            boolean syncSuccess = keycloakAdminService.updateUserCompleteProfile(
                updatedUser.getUserName(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getProfileImage()
            );
            
            if (syncSuccess) {
                System.out.println("Successfully synced user profile to Keycloak: " + updatedUser.getUserName());
            } else {
                System.err.println("Failed to sync user profile to Keycloak: " + updatedUser.getUserName());
            }
        } catch (Exception e) {
            System.err.println("Error syncing to Keycloak: " + e.getMessage());
            // Don't throw exception - database update should still succeed even if Keycloak sync fails
        }
        
        System.out.println("=== UPDATE BY ID COMPLETED ===");
        return updatedUser;
    }

    @Transactional
    public User deleteById(Long id){
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        this.userRepository.delete(user);
        this.userRepository.flush();
        checkAndResetAutoIncrement();
        return user;
    }

    @Transactional
    public void checkAndResetAutoIncrement() {
        long count = this.userRepository.count();
        if (count == 0) {
            try {
                entityManager.flush();
                entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();
            } catch (Exception e) {
                System.err.println("Failed to reset auto-increment: " + e.getMessage());
            }
        }
    }
}
