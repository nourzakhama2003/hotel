package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.dtos.HotelUpdateDto;
import com.nourproject.hotel.entities.Hotel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    Hotel dtoToHotel(HotelDto hoteldto);
    HotelDto hotelToHotelDto(Hotel hotel);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     void hotelUpdateDtoToHotel(HotelUpdateDto hotelUpdateDto,@MappingTarget Hotel hotel);
}
