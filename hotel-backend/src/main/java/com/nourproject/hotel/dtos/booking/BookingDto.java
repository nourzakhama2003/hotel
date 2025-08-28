package com.nourproject.hotel.dtos.booking;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.enums.PayementStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class BookingDto {

    private Long id;


    private BookingStatus bookingStatus;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;




    private String bookingRefrence;
    private BigDecimal totalPrice;
    private Long userId;
    private Long roomId;
    private Long paymentId;
    private PayementStatus payementStatus=PayementStatus.PENDING;
    private final LocalDateTime createAt=LocalDateTime.now();
}
