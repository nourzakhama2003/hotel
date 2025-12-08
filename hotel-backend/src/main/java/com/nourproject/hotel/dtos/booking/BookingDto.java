package com.nourproject.hotel.dtos.booking;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
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
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class BookingDto {

    private Long id;




    @NotNull(message="check in date required")
    private LocalDateTime checkInDate;
    @NotNull(message="check out date required")
    private LocalDateTime checkOutDate;


    private String bookingReference;
//@NotNull(message="total price required")
//@DecimalMin(value = "0.1",message = "price must be greater than 0.1")
    private BigDecimal totalPrice;



private Long userId;
private Long roomId;
private PaymentDto payment;


private BookingStatus bookingStatus;
  @Builder.Default
    private PaymentStatus paymentStatus=PaymentStatus.PENDING;
  @Builder.Default
    private final  LocalDateTime createAt=LocalDateTime.now();
}
