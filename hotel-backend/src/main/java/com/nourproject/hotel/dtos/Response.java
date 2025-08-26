package com.nourproject.hotel.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Response {
    //generic
    private int status;
    private String message;



    //user data output
    private UserDto user;
    private List<UserDto> users;

    //Booking data output
    private BookingDto booking;
    private List<BookingDto> bookings;

    //Room data output
    private RoomDto room;
    private List<RoomDto> rooms;

    //Hotel data output
    private HotelDto hotel;
    private List<HotelDto> hotels;

    //Payment data output
    private PaymentDto payment;
    private List<PaymentDto> payments;

    //Payment data output
    private NotificationDto notification;
    private List<NotificationDto> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();


}
