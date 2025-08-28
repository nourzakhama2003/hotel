package com.nourproject.hotel.dtos.payment;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.enums.PayementGateway;

import java.time.LocalDateTime;

public class PaymentUpdateDto {
    private Long Id;
    private BookingDto booking;
    private String transactionId;
    private double amount;
    private LocalDateTime paymentDate;
    private String bookingRefrence;
    private String  failueReason;
    private PayementGateway paymentGateway;
    private String approvalLink; //paypal payment approval UEL
}
