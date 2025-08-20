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
        return updateById(user.getId(), userUpdateDto);
    }

    public User syncFromKeycloak(UserDto keycloakData) {
        User existingUser = findByUserName(keycloakData.getUserName());

        if (existingUser != null) {
            UserUpdateDto updateDto = userMapper.userDtoToUserUpdateDto(keycloakData);
            return updateById(existingUser.getId(), updateDto);
        } else {
            return save(keycloakData);
        }
    }

    public User updateById(Long id, UserUpdateDto userUpdateDto){
        System.out.println("Updating user with ID: " + id);
        System.out.println("Update data: " + userUpdateDto);
        
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        
        System.out.println("Found user: " + user.getUserName());
        
        // Store original values to detect changes
        String originalFirstName = user.getFirstName();
        String originalLastName = user.getLastName();
        
        // Update the user entity
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
        User updatedUser = this.userRepository.save(user);
        
        System.out.println("User updated successfully: " + updatedUser.getUserName());
        System.out.println("Profile image updated: " + (updatedUser.getProfileImage() != null ? "Yes" : "No"));
        
        // Check if firstName or lastName changed and sync to Keycloak
        boolean nameChanged = false;
        if ((userUpdateDto.getFirstName() != null && !userUpdateDto.getFirstName().equals(originalFirstName)) ||
            (userUpdateDto.getLastName() != null && !userUpdateDto.getLastName().equals(originalLastName))) {
            nameChanged = true;
        }
        
        if (nameChanged && updatedUser.getUserName() != null) {
            try {
                System.out.println("🔄 Syncing name changes to Keycloak for user: " + updatedUser.getUserName());
                System.out.println("   First Name: " + originalFirstName + " -> " + updatedUser.getFirstName());
                System.out.println("   Last Name: " + originalLastName + " -> " + updatedUser.getLastName());
                
                boolean syncSuccess = keycloakAdminService.updateUserProfile(
                    updatedUser.getUserName(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName()
                );
                
                if (syncSuccess) {
                    System.out.println("✅ Successfully synced name changes to Keycloak for user: " + updatedUser.getUserName());
                } else {
                    System.err.println("❌ Failed to sync name changes to Keycloak for user: " + updatedUser.getUserName());
                }
            } catch (Exception e) {
                System.err.println("💥 Error syncing to Keycloak: " + e.getMessage());
                e.printStackTrace();
                // Don't fail the database update if Keycloak sync fails
            }
        } else {
            System.out.println("ℹ️ No name changes detected, skipping Keycloak sync");
        }
        
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
