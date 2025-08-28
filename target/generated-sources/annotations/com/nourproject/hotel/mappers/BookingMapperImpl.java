package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;
import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.entities.Room;
import com.nourproject.hotel.entities.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public Booking bookingDtoToBooking(BookingDto bookingDto) {
        if ( bookingDto == null ) {
            return null;
        }

        Booking.BookingBuilder booking = Booking.builder();

        booking.id( bookingDto.getId() );
        booking.payementStatus( bookingDto.getPayementStatus() );
        booking.totalPrice( bookingDto.getTotalPrice() );
        booking.checkInDate( bookingDto.getCheckInDate() );
        booking.checkOutDate( bookingDto.getCheckOutDate() );
        booking.bookingRefrence( bookingDto.getBookingRefrence() );
        booking.bookingStatus( bookingDto.getBookingStatus() );

        return booking.build();
    }

    @Override
    public BookingDto bookingToBookingDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDto.BookingDtoBuilder bookingDto = BookingDto.builder();

        bookingDto.userId( bookingUserId( booking ) );
        bookingDto.roomId( bookingRoomId( booking ) );
        bookingDto.id( booking.getId() );
        bookingDto.bookingStatus( booking.getBookingStatus() );
        bookingDto.checkInDate( booking.getCheckInDate() );
        bookingDto.checkOutDate( booking.getCheckOutDate() );
        bookingDto.bookingRefrence( booking.getBookingRefrence() );
        bookingDto.totalPrice( booking.getTotalPrice() );
        bookingDto.payementStatus( booking.getPayementStatus() );

        return bookingDto.build();
    }

    @Override
    public void updateBookingUpdateDtoToBooking(BookingUpdateDto bookingUpdateDto, Booking booking) {
        if ( bookingUpdateDto == null ) {
            return;
        }

        if ( bookingUpdateDto.getPayementStatus() != null ) {
            booking.setPayementStatus( bookingUpdateDto.getPayementStatus() );
        }
        if ( bookingUpdateDto.getBookingStatus() != null ) {
            booking.setBookingStatus( bookingUpdateDto.getBookingStatus() );
        }
    }

    private Long bookingUserId(Booking booking) {
        User user = booking.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private Long bookingRoomId(Booking booking) {
        Room room = booking.getRoom();
        if ( room == null ) {
            return null;
        }
        return room.getId();
    }
}
