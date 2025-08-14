package com.nourproject.hotel.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user/hotels")
public class HotelController {

    public HotelController() {}

@GetMapping()
    public String getHotels(){
        return "Hello World";
    }
}
