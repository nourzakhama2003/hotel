package com.nourproject.hotel.mappers;

import com.nourproject.hotel.entities.Booking;
import org.mapstruct.*;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;
import com.nourproject.hotel.dtos.booking.BookingDto;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface BookingMapper {

  @Mapping(target="user",ignore=true)
  @Mapping(target="room",ignore=true)
  @Mapping(target="payment",ignore=true)
  @Mapping(target="createAt",ignore=true)
Booking bookingDtoToBooking(BookingDto bookingDto);



  @Mapping(target="userId",source="user.id")
  @Mapping(target="roomId",source="room.id")
  @Mapping(target="payment", source="payment") // Use PaymentMapper for payment mapping
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  BookingDto bookingToBookingDto(Booking booking);



@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(target="user",ignore=true)
@Mapping(target="room",ignore=true)
@Mapping(target="payment",ignore=true)
@Mapping(target="createAt",ignore=true)
@Mapping(target="id",ignore=true)
@Mapping(target="bookingReference",ignore=true) // Booking reference managed by system
@Mapping(target="checkInDate",ignore=true) // Not in BookingUpdateDto
@Mapping(target="checkOutDate",ignore=true) // Not in BookingUpdateDto  
@Mapping(target="totalPrice",ignore=true) // Not in BookingUpdateDto
void updateBookingUpdateDtoToBooking(BookingUpdateDto bookingUpdateDto, @MappingTarget Booking booking);


}
