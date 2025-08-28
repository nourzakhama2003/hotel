package com.nourproject.hotel.mappers;

import com.nourproject.hotel.entities.Booking;
import org.mapstruct.*;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;
import com.nourproject.hotel.dtos.booking.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

  @Mapping(target="user",ignore=true)
  @Mapping(target="room",ignore=true)
  @Mapping(target="payment",ignore=true)
Booking bookingDtoToBooking(BookingDto bookingDto);



  @Mapping(target="userId",source="user.id")
  @Mapping(target="roomId",source="room.id")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
 // @Mapping(target="paymentId",source="payment.id")
  BookingDto bookingToBookingDto(Booking booking);



@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(target="user",ignore=true)
@Mapping(target="room",ignore=true)
@Mapping(target="payment",ignore=true)
void updateBookingUpdateDtoToBooking(BookingUpdateDto bookingUpdateDto, @MappingTarget Booking booking);


}
