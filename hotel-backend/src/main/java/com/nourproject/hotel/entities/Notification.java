package com.nourproject.hotel.entities;

import com.nourproject.hotel.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
   private String subject;
    private String recipient;
    private String body;
    @Enumerated(EnumType.STRING)
   private NotificationType type;
    private String bookingReference;
    private final LocalDateTime createdAt = LocalDateTime.now();
}
