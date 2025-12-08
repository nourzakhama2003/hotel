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
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(notificationDto.getRecipient());
            mail.setSubject(notificationDto.getSubject());
            mail.setText(notificationDto.getBody());
            javaMailSender.send(mail);
            
            log.info("Email sent successfully to: {}", notificationDto.getRecipient());
            
            // Save notification to database
            Notification notification = Notification.builder()
                    .recipient(notificationDto.getRecipient())
                    .subject(notificationDto.getSubject())
                    .body(notificationDto.getBody())
                    .bookingReference(notificationDto.getBookingReference())
                    .type(NotificationType.EMAIL)
                    .build();
            
            notificationRepository.save(notification);
            log.info("Notification saved to database for booking: {}", notificationDto.getBookingReference());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", notificationDto.getRecipient(), e.getMessage());
        }
    }
    public void sendSms() {

    }
    public void sendWhatsApp() {

    }
}
