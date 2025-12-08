package com.nourproject.hotel.dtos.notification;

import com.nourproject.hotel.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class NotificationUpdateDto {
    private Long id;
    private String subject;
    private String recipient;
    private String body;
    private NotificationType type;
    private String bookingReference;
    private LocalDateTime createdAt;

}
