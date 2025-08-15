package com.nourproject.hotel.controllers;


import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.services.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/public/hotels")
public class HotelController {
 HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

@GetMapping()
    public ResponseEntity<List<HotelDto>> getHotels(){
       return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.getHotels());
    }
}
