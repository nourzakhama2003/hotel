package com.nourproject.hotel.notifications;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.entities.Notification;
import com.nourproject.hotel.enums.NotificationType;
import com.nourproject.hotel.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;



    @Async
    public void sendEmail(NotificationDto notificationDto) {
SimpleMailMessage mail = new SimpleMailMessage();
mail.setTo(notificationDto.getRecipient());
mail.setSubject(notificationDto.getSubject ());
mail.setText(notificationDto.getBody ());
javaMailSender.send(mail);

Notification notification=Notification.builder()
                                    .recipient(notificationDto.getRecipient())
                                      .subject(notificationDto.getSubject())
                                        .body(notificationDto.getBody())
                                            .bookingReference(notificationDto.getBookingReference())
                                            .type(NotificationType.EMAIL)
                                    .build();
    }
    public void sendSms() {

    }
    public void sendWhatsApp() {

    }
}
