package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.PayementGateway;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

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
    @JoinColumn(name="payment")
    private Booking booking;
    private String transactionId;

    @DecimalMin(value = "0.1",message = "amount must be greater than 0.1")
    private double amount;
    private final LocalDateTime paymentDate=LocalDateTime.now();
    private String bookingRefrence;

private String  failueReason;
    @Enumerated(EnumType.STRING)
    private PayementGateway paymentGateway;




}
