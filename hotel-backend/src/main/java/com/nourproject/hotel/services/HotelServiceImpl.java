package com.nourproject.hotel.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.nourproject.hotel.dtos.hotel.HotelDto;
import com.nourproject.hotel.dtos.hotel.HotelUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.mappers.HotelMapper;
import com.nourproject.hotel.repositories.HotelRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements com.nourproject.hotel.services.interfaces.HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    public Response findAll(){
        List<HotelDto> hotelList = this.hotelRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(hotelMapper::hotelToHotelDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Hotels retrieved successfully")
                .hotels(hotelList)
                .build();
    }

    public Response findHotelById(Long id){
        HotelDto hotelDto = this.hotelRepository.findById(id)
                .map(hotelMapper::hotelToHotelDto)
                .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("Hotel retrieved successfully")
                .hotel(hotelDto)
                .build();
    }


    public Response saveHotel(HotelDto hotelDto){
        Hotel hotel = this.hotelRepository.save(this.hotelMapper.dtoToHotel(hotelDto));
        return Response.builder()
                .status(201)
                .message("Hotel saved successfully")
                .hotel(hotelMapper.hotelToHotelDto(hotel))
                .build();
    }

    public Response updateHotelById(Long id, HotelUpdateDto hotelUpdateDto){
        Hotel hotel = this.hotelRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
        this.hotelMapper.hotelUpdateDtoToHotel(hotelUpdateDto, hotel);
        Hotel updatedHotel = this.hotelRepository.save(hotel);
        return Response.builder()
                .status(200)
                .message("Hotel updated successfully")
                .hotel(hotelMapper.hotelToHotelDto(updatedHotel))
                .build();
    }



    @Transactional
    public Response deleteById(Long id){
        Hotel hotel = this.hotelRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Hotel with ID " + id + " not found"));
        
        HotelDto deletedHotelDto = hotelMapper.hotelToHotelDto(hotel);
        this.hotelRepository.delete(hotel);
        this.hotelRepository.flush();
        resetAutoIncrementId();
        
        return Response.builder()
                .status(200)
                .message("Hotel deleted successfully")
                .hotel(deletedHotelDto)
                .build();
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
