package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.hotel.HotelDto;
import com.nourproject.hotel.dtos.hotel.HotelUpdateDto;
import com.nourproject.hotel.dtos.Response;

public interface HotelService {
    Response findAll();
    Response findHotelById(Long id);
    Response saveHotel(HotelDto hotelDto);
    Response updateHotelById(Long id, HotelUpdateDto hotelUpdateDto);
    Response deleteById(Long id);

}
