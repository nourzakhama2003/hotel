package com.nourproject.hotel.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="hotels")
@ToString(exclude = "rooms") // Exclude circular references
@EqualsAndHashCode(exclude = "rooms") // Exclude circular references
public class Hotel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name="hotelName")
    private String hotelName;
    @Column(name="hotelLocation")
    private String hotelLocation;

    @Column(columnDefinition = "LONGTEXT")
    private String hotelImage;
    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Room> rooms=new ArrayList<Room>();
}
