package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="user name required")
    @Column(unique = true)
    private String userName;

    @NotBlank(message="email  required")
    @Column(unique = true)
    private String  email;
    private String firstName;
    private String lastName;


    @Enumerated(EnumType.STRING)
    private UserRole role ;
    private Boolean isActive=false;
    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;
@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
private List <Booking> bookingList=new ArrayList<>();

    public User(String userName, String email, String firstName, String lastName, UserRole role) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
