package com.nourproject.hotel.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.hotel.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {




private Long id;
    @NotBlank(message="user name required")
    private String userName;
    private String  email;
    private String firstName;
    private String lastName;
    private UserRole role=UserRole.User ;
    private Boolean isActive=false;
    private String profileImage;
}
