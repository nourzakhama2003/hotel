package com.nourproject.hotel.dtos;

import com.nourproject.hotel.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    @NotBlank(message="user name required")
    private String userName;
    private String  email;
    private String firstName;
    private String lastName;
    private UserRole role ;
}
