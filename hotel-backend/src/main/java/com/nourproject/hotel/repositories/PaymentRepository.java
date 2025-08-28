package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByBookingRefrence(String bookingReference);
}
