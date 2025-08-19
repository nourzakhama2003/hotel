package com.nourproject.hotel.dtos;

import com.nourproject.hotel.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDto {
    private String userName;
    private String  email;
    private String firstName;
    private String lastName;
    private UserRole role ;
}
