package com.nourproject.hotel.services;
import com.nourproject.hotel.dtos.BookingDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.dtos.UserDto;
import com.nourproject.hotel.dtos.UserUpdateDto;
import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.exceptions.NotFoundException;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.mappers.BookingMapper;
import com.nourproject.hotel.mappers.UserMapper;
import com.nourproject.hotel.repositories.BookingRepository;
import com.nourproject.hotel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @PersistenceContext
    private EntityManager entityManager;


    public Response findAll(){
         List<UserDto> list= this.userRepository.findAll().stream().map(userMapper::userToUserDto).toList();
         return Response.builder()
                 .status(200)
                 .message("list of users getted Successfully")
                 .users(list)
                 .build();
    }

    public Response findById(Long id){
        UserDto userDto=this.userRepository.findById(id).map(userMapper::userToUserDto)
                .orElseThrow(() -> new NotFoundException("user with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("user getted Successfully")
                .user(userDto)
                .build();
    }
    public Response getbookingByUserId(Long id){
        List<BookingDto> bookings=this.bookingRepository.findByUserId(id).stream().map(bookingMapper::bookingToBookingDto).toList();
        return Response.builder()
                .status(200)
                .message("user getted Successfully")
                .bookings(bookings)
                .build();
    }

    public Response save(UserDto userDto ){
        UserDto user=userMapper.userToUserDto(userRepository.save(this.userMapper.userDtoToUser(userDto)));
        return  Response.builder()
                .status(200)
                .message("user saved succefuly")
                .user(user)
                .build();
    }

    public Response findByUserName(String userName) {
        UserDto userDto=this.userRepository.findByUserName(userName).map(userMapper::userToUserDto).orElseThrow(()->new NotFoundException("user with userName "+userName+"notFound"));
        return Response.builder()
                .status(200)
                .message("user getted Successfully")
                .user(userDto)
                .build();
    }
    public User getByUserName(String userName){
        return this.userRepository.findByUserName(userName).orElseThrow(()->new NotFoundException("user with userName admin not found"));
    }
    public Response findByEmail(String userName) {
        UserDto userDto=this.userRepository.findByEmail(userName).map(userMapper::userToUserDto).orElseThrow(()->new NotFoundException("user with userName "+userName+"notFound"));
        return Response.builder()
                .status(200)
                .message("user getted Successfully")
                .user(userDto)
                .build();
    }
    public Response updateByUsername(String username, UserUpdateDto userUpdateDto) {
        User user = this.userRepository.findByUserName(username)
                .orElseThrow(() -> new GlobalException("User with username " + username + " not found"));
        return updateById(user.getId(), userUpdateDto);
    }

    public Response createOrUpdateUser(UserDto userDto) {
        // Use the safe method that returns null instead of throwing exception
        User existingUser = this.userRepository.findByUserName(userDto.getUserName()).orElse(null);

        if (existingUser != null) {
            // User exists, update them
            UserUpdateDto updateDto = userMapper.userDtoToUserUpdateDto(userDto);
            return updateById(existingUser.getId(), updateDto);
        } else {
            // User doesn't exist, create new one
            return save(userDto);
        }
    }

    public Response updateById(Long id, UserUpdateDto userUpdateDto){
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        this.userMapper.updateUserUpdateDtoToUser(userUpdateDto, user);
       UserDto  updatedUser=this.userMapper.userToUserDto(this.userRepository.save(user));

             keycloakAdminService.updateUserCompleteProfile(
                updatedUser.getUserName(),
                updatedUser.getFirstName(),
                updatedUser.getLastName()
            );

        return Response.builder()
                .status(200)
                .message("user updated Successfully")
                .user(updatedUser)
                .build();
    }

    @Transactional
    public Response deleteByUserId(Long id){
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("user with ID " + id + " not found"));
        
        // Store user data for response and Keycloak deletion
        UserDto deletedUserDto = userMapper.userToUserDto(user);
        String userEmail = user.getEmail();
        String username = user.getUserName();
        
        // Delete from local database first
        this.userRepository.delete(user);
        this.userRepository.flush();
        checkAndResetAutoIncrement();
        
        // Then delete from Keycloak using email
        try {
            System.out.println("Attempting to delete user from Keycloak with email: " + userEmail);
            boolean keycloakDeleteSuccess = keycloakAdminService.deleteUserByEmail(userEmail);
            
            if (keycloakDeleteSuccess) {
                System.out.println("Successfully deleted user from both database and Keycloak: " + username);
            } else {
                System.err.println("User deleted from database but failed to delete from Keycloak: " + username);
                // Note: We don't rollback the database transaction even if Keycloak deletion fails
                // This ensures data consistency (user is deleted) even if Keycloak sync fails
            }
        } catch (Exception e) {
            System.err.println("Error deleting user from Keycloak: " + e.getMessage());
            // Database deletion still succeeded, so we continue
        }
        
        return Response.builder()
                .status(200)
                .message("user deleted Successfully")
                .user(deletedUserDto)
                .build();
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
