package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
List<Booking> findByUserUserName(String userName);
    Optional<Booking> findByBookingReference(String bookingRefrence);

            @Query("""
         select case when count(b)=0 then true else false end 
         from Booking b
         where b.room.id=:roomId
         and :checkInDate <= b.checkOutDate
         and :checkOutDate >= b.checkInDate
         and b.bookingStatus in ('BOOKED','CHECKED_IN')
        """)
    Boolean isRoomAvailableForBooking(@Param("roomId")Long roomId, @Param("checkInDate") LocalDateTime checkInDate, @Param("checkOutDate") LocalDateTime checkOutDate);
}