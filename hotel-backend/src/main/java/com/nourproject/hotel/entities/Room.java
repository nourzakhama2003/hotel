package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="rooms")
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Min(value = 1,message = "room number must be greater than 0")
  @Column(unique = true)
  private int roomNumber;
  @Min(value = 1,message = "capacity must be greater than 0")
  private int capacity;
  @Enumerated(EnumType.STRING)
  private RoomType type;
  @DecimalMin(value = "0.1",message = "price must be greater than 0.1")
  private double pricePerNight;
  private String description;
    @ManyToOne
    @JoinColumn(name="hotel_id")
    private Hotel hotel;


    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Booking> bookingList=new ArrayList<>();


    @Column(columnDefinition = "LONGTEXT")
    private String roomImage;

}
