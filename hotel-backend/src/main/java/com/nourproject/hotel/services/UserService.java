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

    @PersistenceContext
    private EntityManager entityManager;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto,user);
        return this.userRepository.save(user);
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
