package com.nourproject.hotel.services;

import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.Room;
import com.nourproject.hotel.enums.RoomType;
import com.nourproject.hotel.exceptions.InvalidBookingStateOrDate;
import com.nourproject.hotel.mappers.RoomMapper;
import com.nourproject.hotel.repositories.HotelRepository;
import com.nourproject.hotel.repositories.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.exceptions.GlobalException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements com.nourproject.hotel.services.interfaces.RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final HotelRepository hotelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Response findAllRooms(){
        List<RoomDto> roomList = this.roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(roomMapper::roomToRoomDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("rooms retrieved successfully")
                .rooms(roomList)
                .build();
    }

    public Response findRoomById(Long id){
        RoomDto roomDto = this.roomRepository.findById(id)
                .map(roomMapper::roomToRoomDto)
                .orElseThrow(() -> new GlobalException("room with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("room retrieved successfully")
                .room(roomDto)
                .build();
    }
public Response searchRoom(String search){
        List<RoomDto> roomList = this.roomRepository.searchRoom(search)
               .stream()
               .map(roomMapper::roomToRoomDto)
               .toList();
        return Response.builder()
                .status(200)
                .message("rooms retrieved successfully")
                .rooms(roomList)
                .build();
}
public Response getAvailableRooms(LocalDateTime checkInDate, LocalDateTime checkOutDate, RoomType roomType) {
        if(checkInDate.isBefore(LocalDateTime.now())){
            throw new InvalidBookingStateOrDate("checkInDate must be after now");
        }

        if(checkInDate.isAfter(checkOutDate)){
            throw new InvalidBookingStateOrDate("checkInDate must be before checkOutDate");
        }
        if(checkInDate.isEqual(checkOutDate)){
            throw new InvalidBookingStateOrDate("checkInDate must be before checkOutDate");
        }


        List<RoomDto> roomList=this.roomRepository.findAvailableRoom(checkInDate,checkOutDate,roomType).stream().map(roomMapper::roomToRoomDto).toList();
    return Response.builder()
            .status(200)
            .message("rooms retrieved successfully")
            .rooms(roomList)
            .build();
    }
    public Response saveRoom(RoomDto roomDto,Long hotelId){
        Hotel hotel=this.hotelRepository.findById(hotelId).orElseThrow(()->new GlobalException("hotel with ID "+hotelId+" not found"));
          Room room=this.roomMapper.roomDtoToRoom(roomDto);
          room.setHotel(hotel);
        Room  savedRoom = this.roomRepository.save(room);


        return Response.builder()
                .status(201)
                .message("room saved successfully")
                .room(roomMapper.roomToRoomDto(savedRoom))
                .build();
    }

    public Response updateRoomById(Long id, RoomUpdateDto roomUpdateDto){
        Room room = this.roomRepository.findById(id)
                .orElseThrow(() -> new GlobalException("room with ID " + id + " not found"));
        this.roomMapper.updateRoomDtoToRoom(roomUpdateDto, room);
        Room savedRooom = this.roomRepository.save(room);
        return Response.builder()
                .status(200)
                .message("room updated successfully")
                .room(roomMapper.roomToRoomDto(savedRooom))
                .build();
    }



    @Transactional
    public Response deleteById(Long id){
        Room room = this.roomRepository.findById(id)
                .orElseThrow(() -> new GlobalException("room with ID " + id + " not found"));

        RoomDto deletedRoomDto = roomMapper.roomToRoomDto(room);
        this.roomRepository.delete(room);
        this.roomRepository.flush();
        resetAutoIncrementId();

        return Response.builder()
                .status(200)
                .message("Hotel deleted successfully")
                .room(deletedRoomDto)
                .build();
    }


    @Transactional
    public void resetAutoIncrementId(){
        if(this.roomRepository.count() == 0) {
            try {
                // Use the injected entityManager instance, not the class
                entityManager.createNativeQuery("ALTER TABLE hotels AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();

            } catch (Exception e) {
                System.err.println("❌ Failed to reset room auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset room auto-increment: " + e.getMessage());
            }
        }
    }




}
