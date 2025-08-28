package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.*;
import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;

import java.time.LocalDateTime;

public interface BookingService {
    Response findAllBookings();
    Response  findByUserUserName(String userName);
    Response findbookingByUserId(Long id);
    Response findbookingBookingRefrence(String refrence);
    Boolean isRoomAvailableForBooking(Long roomId, LocalDateTime checkInDate, LocalDateTime checkOutDate);
    Response findbookingById(Long id);
    Response saveBooking(BookingDto bookingDto, Long userId, Long roomId);
    Response updateBookingById(Long id, BookingUpdateDto bookingUpdateDto);
    Response deleteById(Long id);
}
