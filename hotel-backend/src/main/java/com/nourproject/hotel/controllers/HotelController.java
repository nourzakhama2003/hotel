package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.hotel.HotelDto;
import com.nourproject.hotel.dtos.hotel.HotelUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.mappers.HotelMapper;
import com.nourproject.hotel.services.HotelServiceImpl;
import com.nourproject.hotel.services.interfaces.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelMapper hotelMapper;
    private final HotelService hotelService;

    @GetMapping()
    public ResponseEntity<Response> getHotels(){
        return ResponseEntity.ok(this.hotelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getHotelById(@PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.ok(this.hotelService.findHotelById(id));
    }


    @PostMapping()
    public ResponseEntity<Response> addHotel(@Valid @RequestBody HotelDto hotelDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.hotelService.saveHotel(hotelDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateHotelById(@PathVariable(value = "id", required = true) Long id, 
                                                   @Valid @RequestBody HotelUpdateDto hotelUpdateDto){
        return ResponseEntity.ok(this.hotelService.updateHotelById(id, hotelUpdateDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteHotelById(@PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.ok(this.hotelService.deleteById(id));
    }

}
