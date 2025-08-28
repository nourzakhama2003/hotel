package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.dtos.user.UserDto;
import com.nourproject.hotel.dtos.user.UserUpdateDto;
import com.nourproject.hotel.entities.User;
import com.nourproject.hotel.mappers.UserMapper;
import com.nourproject.hotel.services.UserServiceImpl;
import com.nourproject.hotel.services.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public/users")
public class UserController {
    private final UserMapper userMapper;
    UserService userService;

    public UserController(UserServiceImpl userService, UserMapper userMapper ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Response> getusers(){
        return ResponseEntity.ok(this.userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable(value = "id",required = true) Long id){
     return ResponseEntity.ok(userService.findById(id));
    }
    @GetMapping("booking/{id}")
    public ResponseEntity<Response> getbookingByUserId(@PathVariable(value = "id",required = true) Long id){
   return ResponseEntity.ok(this.userService.getbookingByUserId(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Response> getUserByUsername(@PathVariable("username") String username) {
     return ResponseEntity.ok(this.userService.findByUserName(username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Response> getUserByEmail(@PathVariable("email") String email) {
  return ResponseEntity.ok(this.userService.findByEmail(email));
    }



    @PostMapping()
    public ResponseEntity<Response> createOrUpdateUser(@Valid @RequestBody UserDto userDto) {
    return ResponseEntity.ok(this.userService.createOrUpdateUser(userDto));
    }


    @PutMapping("/{id}")
    ResponseEntity<Response> updateUserById(@PathVariable(value = "id",required = true) Long id, @Valid @RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateById(id,userUpdateDto)) ;
    }
    
    @PutMapping("/username/{username}")
    public ResponseEntity<Response> updateUserByUsername(@PathVariable("username") String username, @RequestBody UserUpdateDto userUpdateDataDto) {

                User user=this.userService.getByUserName(username);
                return ResponseEntity.ok(this.userService.updateById(user.getId(), userUpdateDataDto));


    }
    
    @DeleteMapping("/{id}")
    ResponseEntity<Response> deleteUserById(@PathVariable(value = "id",required = true) Long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.deleteByUserId(id));
    }
    


    


    



}

