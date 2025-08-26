package com.nourproject.hotel.mappers;

import com.nourproject.hotel.entities.Booking;
import org.mapstruct.Mapper;
import  com.nourproject.hotel.dtos.BookingDto;
@Mapper(componentModel = "spring")
public interface BookingMapper {
Booking bookingDtoToBooking(BookingDto bookingDto);
  BookingDto bookingToBookingDto(Booking booking);


}
