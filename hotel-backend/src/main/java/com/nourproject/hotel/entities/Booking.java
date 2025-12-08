package com.nourproject.hotel.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="bookings")
@ToString(exclude = {"user", "room", "payment"}) // Exclude circular references
@EqualsAndHashCode(exclude = {"user", "room", "payment"}) // Exclude circular references
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    @JsonBackReference("room-bookings")
    private Room room;
    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @DecimalMin(value = "0.1",message = "price must be greater than 0.1")
    private BigDecimal totalPrice;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    private String bookingReference;

    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (this.createAt == null) {
            this.createAt = LocalDateTime.now();
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
        if (this.bookingStatus == null) {
            this.bookingStatus = BookingStatus.PENDING;
        }
    }
}
