package com.nourproject.hotel.dtos.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.enums.RoomType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class RoomDto {

    @NotNull(message="room type required")
    @Min(value = 1,message = "room number must be greater than 0")
    private int roomNumber;
    @NotNull(message="room type required")
    @Min(value = 1,message = "capacity must be greater than 0")
    private int capacity;
    @NotNull(message="room type required")
    private RoomType type;
    @NotNull(message="room type required")
    @DecimalMin(value = "0.1",message = "price must be greater than 0.1")
    private BigDecimal pricePerNight;

    private String description;

    private String roomImage;
    private final LocalDateTime createAt=LocalDateTime.now();
    // Remove hotel reference to avoid circular dependency
    // If you need hotel info, use hotelId instead
    private Long hotelId;
   private List<BookingDto> bookings;

}
