package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Room;
import com.nourproject.hotel.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query(""" 
SELECT r FROM Room r
   where  r.id NOT IN(
  SELECT b.room.id   FROM Booking b
  WHERE :checkInDate<=b.checkInDate
  AND :checkOutDate<=b.checkOutDate
  AND b.bookingStatus IN( 'BOOKED','CHECKED_IN')
   ) 
   AND (:roomType IS NULL OR r.type=:roomType)
        """)
    List<Room> findAvailableRoom(@Param("checkInDate") LocalDateTime checkInDate, @Param("checkOutDate") LocalDateTime checkOutDate, @Param("roomType") RoomType roomType);


    @Query("""
select r from Room r 
where cast(r.roomNumber as String) like %:searchItem%
  or cast(r.capacity as String) like %:searchItem%
      or lower(r.type) like lower(:searchItem)
      or cast(r.pricePerNight as String)like %:searchItem%
      or  lower(r.description) like lower(concat('%',:searchItem,'%'))
""")
    List<Room> searchRoom(@Param("searchItem")String searchItem);

}
