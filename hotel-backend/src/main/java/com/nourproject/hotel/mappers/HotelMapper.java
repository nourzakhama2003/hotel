package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.entities.Hotel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    Hotel dtoToHotel(HotelDto hoteldto);
    HotelDto hotelToHotelDto(Hotel hotel);
}
