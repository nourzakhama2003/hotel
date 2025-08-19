package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String  email;
    private String firstName;
    private String lastName;


    @Enumerated(EnumType.STRING)
    private UserRole role ;

    private String profileImage;


    public User(String userName, String email, String firstName, String lastName, UserRole role) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
