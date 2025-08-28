package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.dtos.room.RoomUpdateDto;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.entities.Room;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Override
    public Room roomDtoToRoom(RoomDto roomDto) {
        if ( roomDto == null ) {
            return null;
        }

        Room.RoomBuilder room = Room.builder();

        room.roomNumber( roomDto.getRoomNumber() );
        room.capacity( roomDto.getCapacity() );
        room.type( roomDto.getType() );
        room.pricePerNight( roomDto.getPricePerNight() );
        room.description( roomDto.getDescription() );
        room.createAt( roomDto.getCreateAt() );
        room.roomImage( roomDto.getRoomImage() );

        return room.build();
    }

    @Override
    public RoomDto roomToRoomDto(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomDto.RoomDtoBuilder roomDto = RoomDto.builder();

        roomDto.hotelId( roomHotelId( room ) );
        roomDto.roomNumber( room.getRoomNumber() );
        roomDto.capacity( room.getCapacity() );
        roomDto.type( room.getType() );
        roomDto.pricePerNight( room.getPricePerNight() );
        roomDto.description( room.getDescription() );
        roomDto.roomImage( room.getRoomImage() );

        return roomDto.build();
    }

    @Override
    public void updateRoomDtoToRoom(RoomUpdateDto roomDto, Room room) {
        if ( roomDto == null ) {
            return;
        }

        if ( roomDto.getId() != null ) {
            room.setId( roomDto.getId() );
        }
        room.setRoomNumber( roomDto.getRoomNumber() );
        room.setCapacity( roomDto.getCapacity() );
        if ( roomDto.getType() != null ) {
            room.setType( roomDto.getType() );
        }
        if ( roomDto.getPricePerNight() != null ) {
            room.setPricePerNight( roomDto.getPricePerNight() );
        }
        if ( roomDto.getDescription() != null ) {
            room.setDescription( roomDto.getDescription() );
        }
        if ( roomDto.getRoomImage() != null ) {
            room.setRoomImage( roomDto.getRoomImage() );
        }
    }

    private Long roomHotelId(Room room) {
        Hotel hotel = room.getHotel();
        if ( hotel == null ) {
            return null;
        }
        return hotel.getId();
    }
}
