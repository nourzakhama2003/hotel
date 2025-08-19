package com.nourproject.hotel.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.nourproject.hotel.dtos.HotelDto;
import com.nourproject.hotel.dtos.HotelUpdateDto;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.mappers.HotelMapper;
import com.nourproject.hotel.repositories.HotelRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    HotelRepository hotelRepository;
    HotelMapper hotelMapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    public List<Hotel> findAllHotels(){
        return this.hotelRepository.findAll();
    }


    public Hotel findHotelById(Long id){
        return this.hotelRepository.findById(id)
            .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
    }

    public Hotel saveHotel(HotelDto hoteldto ){
        return this.hotelRepository.save(this.hotelMapper.dtoToHotel(hoteldto));
    }
    public Hotel updateHotelById(Long id, HotelUpdateDto hotelUpdateDto){
        Hotel hotel = this.hotelRepository.findById(id)
            .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
     this.hotelMapper.hotelUpdateDtoToHotel(hotelUpdateDto,hotel);
        return this.hotelRepository.save(hotel);
    }



    @Transactional
    public Hotel deleteById(Long id){
        Hotel hotel = this.hotelRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
        
        this.hotelRepository.delete(hotel);
        this.hotelRepository.flush();
        resetAutoIncrementId();
        
        return hotel;
    }


    @Transactional
    public void resetAutoIncrementId(){
        if(this.hotelRepository.count() == 0) {
            try {
                // Use the injected entityManager instance, not the class
                entityManager.createNativeQuery("ALTER TABLE hotels AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();

            } catch (Exception e) {
                System.err.println("❌ Failed to reset hotel auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset hotel auto-increment: " + e.getMessage());
            }
        }
    }




}
