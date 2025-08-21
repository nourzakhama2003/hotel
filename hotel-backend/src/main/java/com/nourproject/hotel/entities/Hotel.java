package com.nourproject.hotel.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name="hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name="hotelName")
    private String hotelName;
    @OneToMany(mappedBy = "hotel" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Room> rooms=new ArrayList<Room>();
}
