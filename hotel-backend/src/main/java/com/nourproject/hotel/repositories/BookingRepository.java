package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByBookingRefrence(String bookingRefrence);

            @Query("""
         select case when count(b)=0 then true else false end 
         from Booking b
         where b.room.id=:roomId
         and :checkInDate<=b.checkInDate
         and :checkOutDate<=b.checkOutDate
         and b.bookingStatus in ('BOOKED','CHECKED_IN')
        """)
    Boolean isAvailable(@Param("roomId")Long roomId, @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate);
}