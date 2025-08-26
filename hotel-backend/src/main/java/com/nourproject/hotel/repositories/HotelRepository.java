package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
   Optional<Hotel> findById(Long hotelId);
}
