package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.entities.Room;
import org.mapstruct.*;
import com.nourproject.hotel.dtos.room.RoomDto;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface RoomMapper {
    
    @Mapping(target = "hotel", ignore = true) // Handle hotel assignment manually in service
    @Mapping(target = "bookingList", ignore = true) // Don't map bookings during room creation
    @Mapping(target = "createAt", ignore = true) // Creation timestamp set by entity lifecycle
    Room roomDtoToRoom(RoomDto roomDto);
    
    @Mapping(target = "hotelId", source = "hotel.id") // Map hotel.id to hotelId
    @Mapping(target = "bookings", ignore = true) // Ignore bookings to avoid circular dependencies
    RoomDto roomToRoomDto(Room room);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "hotel", ignore = true) // Don't update hotel via DTO
    @Mapping(target = "bookingList", ignore = true) // Don't update bookings via room DTO
    @Mapping(target = "createAt", ignore = true) // Don't update creation timestamp
    void updateRoomDtoToRoom(RoomUpdateDto roomDto, @MappingTarget Room room);
}
