package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.user.UserDto;
import com.nourproject.hotel.dtos.user.UserUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.User;

public interface UserService {
    Response findAll();
    User getByUserName(String usernam);
    User getByEmail(String email);
    Response findById(Long id);
    Response findByUserName(String username);
    Response findByEmail(String email);
    Response save(UserDto userDto);
    Response updateById(Long id, UserUpdateDto userUpdateDto);
    Response deleteByUserId(Long id);
    Response getbookingByUserId(Long id);
    Response createOrUpdateUser(UserDto userDto);
}
