package com.nourproject.hotel.notifications;

import com.nourproject.hotel.dtos.notification.NotificationDto;

public interface NotificationService {
    void sendEmail(NotificationDto notification);
    void sendSms();
    void sendWhatsApp();
}
