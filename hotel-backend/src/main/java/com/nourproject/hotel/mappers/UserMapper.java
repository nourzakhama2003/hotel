package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.user.UserDto;
import com.nourproject.hotel.dtos.user.UserUpdateDto;
import com.nourproject.hotel.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    User userDtoToUser(UserDto userDto);
    
    @Mapping(target = "bookings", ignore = true)
    UserDto userToUserDto(User user);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "userName", ignore = true)
    void updateUserUpdateDtoToUser(UserUpdateDto userUpdateDto, @MappingTarget User user);
    
    /**
     * Convert UserDto to UserUpdateDto for reusing update logic
     * This eliminates code duplication in sync operations
     */
    UserUpdateDto userDtoToUserUpdateDto(UserDto userDto);
}
