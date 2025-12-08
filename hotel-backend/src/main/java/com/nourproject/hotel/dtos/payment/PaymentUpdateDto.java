package com.nourproject.hotel.dtos.payment;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.enums.PaymentGateway;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class PaymentUpdateDto {
    private Long Id;
    private BookingDto booking;
    private String transactionId;
    private double amount;
    private LocalDateTime paymentDate;
    private String bookingReference;
    private String  failueReason;
    private PaymentGateway paymentGateway;
    private String approvalLink; //paypal payment approval UEL
}
