package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Long>{
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
}
