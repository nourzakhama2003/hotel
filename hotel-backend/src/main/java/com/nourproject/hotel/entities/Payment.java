package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.PaymentGateway;
import com.nourproject.hotel.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;
    @OneToOne
    @JoinColumn(name="booking_id")
    private Booking booking;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus payementStatus;
    @DecimalMin(value = "0.1",message = "amount must be greater than 0.1")
    private double amount;
    private  LocalDateTime paymentDate;
    private String bookingReference;
private boolean success;
private String  failueReason;
    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;




}
