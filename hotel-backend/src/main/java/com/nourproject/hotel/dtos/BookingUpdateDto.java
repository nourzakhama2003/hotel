package com.nourproject.hotel.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.enums.PayementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class BookingUpdateDto {

    private Long id;
    private UserDto user;
    private RoomDto room;
    private PayementStatus payementStatus;
    private double totalPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String bookingRefrence;
    private BookingStatus bookingStatus;
}
