package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.hotel.HotelDto;
import com.nourproject.hotel.dtos.hotel.HotelUpdateDto;
import com.nourproject.hotel.dtos.room.RoomDto;
import com.nourproject.hotel.entities.Hotel;
import com.nourproject.hotel.entities.Room;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class HotelMapperImpl implements HotelMapper {

    @Override
    public Hotel dtoToHotel(HotelDto hoteldto) {
        if ( hoteldto == null ) {
            return null;
        }

        Hotel.HotelBuilder hotel = Hotel.builder();

        hotel.id( hoteldto.getId() );
        hotel.hotelName( hoteldto.getHotelName() );
        hotel.rooms( roomDtoListToRoomList( hoteldto.getRooms() ) );

        return hotel.build();
    }

    @Override
    public HotelDto hotelToHotelDto(Hotel hotel) {
        if ( hotel == null ) {
            return null;
        }

        HotelDto.HotelDtoBuilder hotelDto = HotelDto.builder();

        hotelDto.id( hotel.getId() );
        hotelDto.hotelName( hotel.getHotelName() );
        hotelDto.rooms( roomListToRoomDtoList( hotel.getRooms() ) );

        return hotelDto.build();
    }

    @Override
    public void hotelUpdateDtoToHotel(HotelUpdateDto hotelUpdateDto, Hotel hotel) {
        if ( hotelUpdateDto == null ) {
            return;
        }

        if ( hotelUpdateDto.getId() != null ) {
            hotel.setId( hotelUpdateDto.getId() );
        }
        if ( hotelUpdateDto.getHotelName() != null ) {
            hotel.setHotelName( hotelUpdateDto.getHotelName() );
        }
    }

    protected Room roomDtoToRoom(RoomDto roomDto) {
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

    protected List<Room> roomDtoListToRoomList(List<RoomDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Room> list1 = new ArrayList<Room>( list.size() );
        for ( RoomDto roomDto : list ) {
            list1.add( roomDtoToRoom( roomDto ) );
        }

        return list1;
    }

    protected RoomDto roomToRoomDto(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomDto.RoomDtoBuilder roomDto = RoomDto.builder();

        roomDto.roomNumber( room.getRoomNumber() );
        roomDto.capacity( room.getCapacity() );
        roomDto.type( room.getType() );
        roomDto.pricePerNight( room.getPricePerNight() );
        roomDto.description( room.getDescription() );
        roomDto.roomImage( room.getRoomImage() );

        return roomDto.build();
    }

    protected List<RoomDto> roomListToRoomDtoList(List<Room> list) {
        if ( list == null ) {
            return null;
        }

        List<RoomDto> list1 = new ArrayList<RoomDto>( list.size() );
        for ( Room room : list ) {
            list1.add( roomToRoomDto( room ) );
        }

        return list1;
    }
}
