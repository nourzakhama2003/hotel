package com.nourproject.hotel.services;


import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.mappers.HotelMapper;
import com.nourproject.hotel.repositories.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {
    HotelRepository hotelRepository;
    HotelMapper hotelMapper;
    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    public List<HotelDto> getHotels(){
        return this.hotelRepository.findAll().stream().map(hotelMapper::hotelToHotelDto).collect(Collectors.toList());
    }
}
