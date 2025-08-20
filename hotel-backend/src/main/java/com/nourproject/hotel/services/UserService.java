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
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
        User updatedUser = this.userRepository.save(user);
        try {
            boolean syncSuccess = keycloakAdminService.updateUserCompleteProfile(
                updatedUser.getUserName(),
                updatedUser.getFirstName(),
                updatedUser.getLastName()
            );
            

        } catch (Exception e) {
            System.err.println("Error syncing to Keycloak: " + e.getMessage());
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
