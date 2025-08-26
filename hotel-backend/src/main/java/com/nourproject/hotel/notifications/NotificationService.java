package com.nourproject.hotel.notifications;

import com.nourproject.hotel.dtos.NotificationDto;

public interface NotificationService {
    void sendEmail(NotificationDto notification);
    void sendSms();
    void sendWhatsApp();
}
