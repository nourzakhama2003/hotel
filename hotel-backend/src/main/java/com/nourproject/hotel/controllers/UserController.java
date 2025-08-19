package com.nourproject.hotel.controllers;



import com.nourproject.hotel.dtos.UserDto;
import com.nourproject.hotel.dtos.UserUpdateDto;
import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.mappers.UserMapper;
import com.nourproject.hotel.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/public/users")
public class UserController {
    private final UserMapper userMapper;
    UserService userService;

    public UserController(UserService userService, UserMapper userMapper ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getHotels(){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getHotelById(@PathVariable(value = "id",required = true) Long id){
        // Let the service throw GlobalException, it will be caught by GlobalExceptionHandler
        User user = this.userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDto userDto){
        try {
            // Use the sync method which handles both create and update logic
            User user = this.userService.syncFromKeycloak(userDto);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<User> syncFromKeycloak(@Valid @RequestBody UserDto userDto) {
        try {
            User syncedUser = this.userService.syncFromKeycloak(userDto);
            return ResponseEntity.status(HttpStatus.OK).body(syncedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("/{id}")
    ResponseEntity<User> updateHotelById(@PathVariable(value = "id",required = true) Long id, @Valid @RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateById(id,userUpdateDto)) ;
    }
    @DeleteMapping("/{id}")
    ResponseEntity<User> deleteHotelById(@PathVariable(value = "id",required = true) Long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.deleteById(id));
    }
    


    



}

