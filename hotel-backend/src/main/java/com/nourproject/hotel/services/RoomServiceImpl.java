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
import com.nourproject.hotel.services.interfaces.RoomService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.services.interfaces.RoomService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {
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
    public Response findRoomNumber(){
        List<Room> rooms = this.roomRepository.findAll(Sort.by(Sort.Direction.DESC, "roomNumber"));

        return rooms.stream()
                .findFirst()
                .map(room -> Response.builder()
                        .status(200)
                        .message("Room found")
                        .roomNumber(room.getRoomNumber()+1)
                        .build())
                .orElse(Response.builder()
                        .status(200)
                        .message("no rooms found")
                        .roomNumber(1)
                        .build());
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

    @Transactional
    public Response updateRoomById(Long id, RoomUpdateDto roomUpdateDto){
        Room room = this.roomRepository.findById(id)
                .orElseThrow(() -> new GlobalException("room with ID " + id + " not found"));
        
        // Store original room number for constraint checking
        int originalRoomNumber = room.getRoomNumber();
        
        // Use mapper to update the room
        roomMapper.updateRoomDtoToRoom(roomUpdateDto, room);
        
        // Check for unique constraint violation only if room number changed and is not null
        if (roomUpdateDto.getRoomNumber() != null && room.getRoomNumber() != originalRoomNumber) {
            boolean roomNumberExists = roomRepository.existsByRoomNumberAndIdNot(
                room.getRoomNumber(), id);
            if (roomNumberExists) {
                throw new GlobalException("Room number " + room.getRoomNumber() + " already exists");
            }
        }
        
        Room savedRoom = this.roomRepository.save(room);
        return Response.builder()
                .status(200)
                .message("room updated successfully")
                .room(roomMapper.roomToRoomDto(savedRoom))
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
                entityManager.createNativeQuery("ALTER TABLE rooms AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();

            } catch (Exception e) {
                System.err.println("❌ Failed to reset room auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset room auto-increment: " + e.getMessage());
            }
        }
    }




}
