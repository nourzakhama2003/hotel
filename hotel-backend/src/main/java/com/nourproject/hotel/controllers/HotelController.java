package com.nourproject.hotel.controllers;


import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.dtos.HotelUpdateDto;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.mappers.HotelMapper;
import com.nourproject.hotel.services.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/public/hotels")
public class HotelController {
    private final HotelMapper hotelMapper;
    HotelService hotelService;

    public HotelController(HotelService hotelService, HotelMapper hotelMapper) {
        this.hotelService = hotelService;
        this.hotelMapper = hotelMapper;
    }

@GetMapping()
    public ResponseEntity<List<Hotel>> getHotels(){
       return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.findAllHotels());
    }

@GetMapping("/{id}")
public ResponseEntity<Hotel> getHotelById(@PathVariable(value = "id",required = true) Long id){
    // Let the service throw GlobalException, it will be caught by GlobalExceptionHandler
    Hotel hotel = this.hotelService.findHotelById(id);
    return ResponseEntity.status(HttpStatus.OK).body(hotel);
}

    @PostMapping()
    public ResponseEntity<?> addHotel(@Valid @RequestBody HotelDto hotelDto){
        try {
            Hotel hotel = this.hotelService.saveHotel(hotelDto);
            if(hotel != null){
                return ResponseEntity.status(HttpStatus.CREATED).body(hotel);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create hotel", "message", "Hotel could not be saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed", "message", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    ResponseEntity<Hotel> updateHotelById(@PathVariable(value = "id",required = true) Long id, @Valid @RequestBody HotelUpdateDto hotelUpdateDto){
       return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.updateHotelById(id,hotelUpdateDto)) ;
    }
    @DeleteMapping("/{id}")
    ResponseEntity<Hotel> deleteHotelById(@PathVariable(value = "id",required = true) Long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.hotelService.deleteById(id));
    }

}
