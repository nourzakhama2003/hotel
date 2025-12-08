package com.nourproject.hotel.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nourproject.hotel.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="rooms")
@ToString(exclude = {"hotel", "bookingList"}) // Exclude circular references
@EqualsAndHashCode(exclude = {"hotel", "bookingList"}) // Exclude circular references
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
  private BigDecimal pricePerNight;
  private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hotel_id")
    @JsonBackReference
    private Hotel hotel;

  @Builder.Default
    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("room-bookings")
    private List<Booking> bookingList=new ArrayList<>();

  private  LocalDateTime createAt;
    @Column(columnDefinition = "LONGTEXT")
    private String roomImage;

}
