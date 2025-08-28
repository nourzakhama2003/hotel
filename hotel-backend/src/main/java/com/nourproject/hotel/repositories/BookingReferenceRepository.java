package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingReferenceRepository extends JpaRepository<BookingReference,Long> {

    Optional<BookingReference> findByReferenceNo(String refrenceNo);
}
