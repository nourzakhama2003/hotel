package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.user.UserDto;
import com.nourproject.hotel.dtos.user.UserUpdateDto;
import com.nourproject.hotel.entities.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User userDtoToUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userName( userDto.getUserName() );
        user.email( userDto.getEmail() );
        user.firstName( userDto.getFirstName() );
        user.lastName( userDto.getLastName() );
        user.role( userDto.getRole() );
        user.isActive( userDto.getIsActive() );
        user.profileImage( userDto.getProfileImage() );

        return user.build();
    }

    @Override
    public UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.userName( user.getUserName() );
        userDto.email( user.getEmail() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.role( user.getRole() );
        userDto.isActive( user.getIsActive() );
        userDto.profileImage( user.getProfileImage() );

        return userDto.build();
    }

    @Override
    public void updateUserUpdateDtoToUser(UserUpdateDto userUpdateDto, User user) {
        if ( userUpdateDto == null ) {
            return;
        }

        if ( userUpdateDto.getFirstName() != null ) {
            user.setFirstName( userUpdateDto.getFirstName() );
        }
        if ( userUpdateDto.getLastName() != null ) {
            user.setLastName( userUpdateDto.getLastName() );
        }
        if ( userUpdateDto.getProfileImage() != null ) {
            user.setProfileImage( userUpdateDto.getProfileImage() );
        }
    }

    @Override
    public UserUpdateDto userDtoToUserUpdateDto(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserUpdateDto.UserUpdateDtoBuilder userUpdateDto = UserUpdateDto.builder();

        userUpdateDto.firstName( userDto.getFirstName() );
        userUpdateDto.lastName( userDto.getLastName() );
        userUpdateDto.profileImage( userDto.getProfileImage() );

        return userUpdateDto.build();
    }
}
