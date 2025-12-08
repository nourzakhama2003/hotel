package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Notification;
import com.nourproject.hotel.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByBookingReference(String bookingReference);
    List<Notification> findByType(NotificationType type);
}
