package com.nourproject.hotel.dtos.notification;

import com.nourproject.hotel.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationUpdateDto {
    private Long id;
    private String subject;
    private String recipient;
    private String body;
    private NotificationType type;
    private String bookingReference;
    private LocalDateTime createdAt;

}
