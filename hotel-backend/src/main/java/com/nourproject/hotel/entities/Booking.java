package com.nourproject.hotel.entities;


import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.enums.PayementStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name="payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private PayementStatus payementStatus;
    @DecimalMin(value = "0.1",message = "price must be greater than 0.1")
    private double totalPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;


    private String bookingRefrence;

    private final LocalDateTime createAt=LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;










}
