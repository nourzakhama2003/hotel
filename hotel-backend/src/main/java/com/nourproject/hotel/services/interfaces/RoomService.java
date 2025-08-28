package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.enums.RoomType;

import java.time.LocalDateTime;

public interface RoomService {
    Response findAllRooms();
    Response findRoomById(Long id);
    Response searchRoom(String search);
    Response saveRoom(RoomDto roomDto, Long hotelId);
    Response updateRoomById(Long id, RoomUpdateDto roomUpdateDto);
    Response deleteById(Long id);
    Response getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, RoomType roomType);
}
