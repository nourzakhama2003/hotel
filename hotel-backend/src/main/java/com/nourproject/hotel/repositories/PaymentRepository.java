package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
