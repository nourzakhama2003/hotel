package com.nourproject.hotel.dtos.payment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nourproject.hotel.enums.PaymentGateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PaymentDto {
    private Long Id;
    private Long bookingId;
    private String transactionId;


    @NotNull(message = "amount is required")
    private BigDecimal amount;
    @NotBlank(message = "booking reference is required")
    private String bookingReference;  // Fixed typo



    private String  failueReason;
    private boolean success;
    private PaymentGateway paymentGateway;
    private String approvalLink; //paypal payment approval UEL


    @Builder.Default
    private  LocalDateTime paymentDate=LocalDateTime.now();
}
