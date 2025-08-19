package com.nourproject.hotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data  // ← This generates getters/setters automatically
public class HotelUpdateDto {
    private String hotelName;  // ← Make fields private with @Data
}
