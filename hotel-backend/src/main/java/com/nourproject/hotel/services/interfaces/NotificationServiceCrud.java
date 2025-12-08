package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.notification.NotificationUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.enums.NotificationType;

public interface NotificationServiceCrud {
    Response findAllNotifications();
    Response findNotificationById(Long id);
    Response findNotificationsByRecipient(String recipient);
    Response findNotificationsByBookingReference(String bookingReference);
    Response findNotificationsByType(NotificationType type);
    Response saveNotification(NotificationDto notificationDto);
    Response updateNotificationById(Long id, NotificationUpdateDto notificationUpdateDto);
    Response deleteById(Long id);
}
