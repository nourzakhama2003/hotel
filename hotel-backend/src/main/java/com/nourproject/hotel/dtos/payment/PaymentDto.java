package com.nourproject.hotel.dtos.payment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.enums.PayementGateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PaymentDto {
    private Long Id;
    private BookingDto booking;


    private String transactionId;
    private double amount;
    private  LocalDateTime paymentDate;
    private String bookingRefrence;
    private String  failueReason;
    private PayementGateway paymentGateway;
    private String approvalLink; //paypal payment approval UEL

}
