package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.entities.Room;
import org.mapstruct.*;
import com.nourproject.hotel.dtos.room.RoomDto;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    
    @Mapping(target = "hotel", ignore = true) // Handle hotel assignment manually in service
    Room roomDtoToRoom(RoomDto roomDto);
    
    @Mapping(target = "hotelId", source = "hotel.id") // Map hotel.id to hotelId
    RoomDto roomToRoomDto(Room room);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "hotel", ignore = true) // Don't update hotel via DTO
    void updateRoomDtoToRoom(RoomUpdateDto roomDto, @MappingTarget Room room);
}
