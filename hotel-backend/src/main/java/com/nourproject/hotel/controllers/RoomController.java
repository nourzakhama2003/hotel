package com.nourproject.hotel.controllers;


import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.enums.RoomType;
import com.nourproject.hotel.mappers.RoomMapper;
import com.nourproject.hotel.services.RoomServiceImpl;
import com.nourproject.hotel.services.interfaces.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/public/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomMapper roomMapper;
    private final RoomService roomService;

    @GetMapping()
    public ResponseEntity<Response> getRoomss(){
        return ResponseEntity.ok(this.roomService.findAllRooms());
    }
@GetMapping("/search")
ResponseEntity<Response> searchRoom(@RequestParam("search") String search){
    return ResponseEntity.ok(roomService.searchRoom(search));
}
@GetMapping("/available")
    public ResponseEntity<Response> getAvailableRoom(@RequestParam("checkInDate") LocalDateTime checkInDate,
                                                     @RequestParam("checkOutDate") LocalDateTime checkOutDate,
                                                     @RequestParam(value = "roomType",required = false) RoomType roomType){
        return ResponseEntity.ok(this.roomService.getAvailableRooms(checkInDate,checkOutDate,roomType));
    }
    @GetMapping("/hotel/{id}")
    public ResponseEntity<Response> getRoomByHotelId(@PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.ok(this.roomService.findAllRooms());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response> getRoomById(@PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.ok(this.roomService.findRoomById(id));
    }

    @PostMapping("/hotel/{id}")
    public ResponseEntity<Response> addRoom(@Valid @RequestBody RoomDto roomDto, @PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roomService.saveRoom(roomDto,id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateRoomById(@PathVariable(value = "id", required = true) Long id,
                                                    @Valid @RequestBody RoomUpdateDto roomUpdateDto){
        return ResponseEntity.ok(this.roomService.updateRoomById(id, roomUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteHotelById(@PathVariable(value = "id", required = true) Long id){
        return ResponseEntity.ok(this.roomService.deleteById(id));
    }

}
