package com.nourproject.hotel.services;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.notification.NotificationUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.Notification;
import com.nourproject.hotel.enums.NotificationType;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.exceptions.NotFoundException;
import com.nourproject.hotel.mappers.NotificationMapper;
import com.nourproject.hotel.repositories.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceCrud {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final JavaMailSender javaMailSender;

    @PersistenceContext
    private EntityManager entityManager;

    public Response findAllNotifications() {
        List<NotificationDto> notificationList = notificationRepository.findAll()
                .stream()
                .map(notificationMapper::notificationToNotificationDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .notifications(notificationList)
                .build();
    }

    public Response findNotificationById(Long id) {
        NotificationDto notificationDto = notificationRepository.findById(id)
                .map(notificationMapper::notificationToNotificationDto)
                .orElseThrow(() -> new NotFoundException("Notification with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("Notification retrieved successfully")
                .notification(notificationDto)
                .build();
    }

    public Response findNotificationsByRecipient(String recipient) {
        List<NotificationDto> notificationList = notificationRepository.findByRecipient(recipient)
                .stream()
                .map(notificationMapper::notificationToNotificationDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .notifications(notificationList)
                .build();
    }

    public Response findNotificationsByBookingReference(String bookingReference) {
        List<NotificationDto> notificationList = notificationRepository.findByBookingReference(bookingReference)
                .stream()
                .map(notificationMapper::notificationToNotificationDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .notifications(notificationList)
                .build();
    }

    public Response findNotificationsByType(NotificationType type) {
        List<NotificationDto> notificationList = notificationRepository.findByType(type)
                .stream()
                .map(notificationMapper::notificationToNotificationDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .notifications(notificationList)
                .build();
    }

    public Response saveNotification(NotificationDto notificationDto) {
        Notification notification = notificationMapper.notificationDtoToNotification(notificationDto);
        Notification savedNotification = notificationRepository.save(notification);

        return Response.builder()
                .status(201)
                .message("Notification saved successfully")
                .notification(notificationMapper.notificationToNotificationDto(savedNotification))
                .build();
    }



    public Response updateNotificationById(Long id, NotificationUpdateDto notificationUpdateDto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification with ID " + id + " not found"));
        
        notificationMapper.updateNotificationUpdateDtoToNotification(notificationUpdateDto, notification);
        Notification savedNotification = notificationRepository.save(notification);
        
        return Response.builder()
                .status(200)
                .message("Notification updated successfully")
                .notification(notificationMapper.notificationToNotificationDto(savedNotification))
                .build();
    }

    @Transactional
    public Response deleteById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification with ID " + id + " not found"));

        NotificationDto deletedNotificationDto = notificationMapper.notificationToNotificationDto(notification);
        notificationRepository.delete(notification);
        notificationRepository.flush();
        resetAutoIncrementId();

        return Response.builder()
                .status(200)
                .message("Notification deleted successfully")
                .notification(deletedNotificationDto)
                .build();
    }

    @Transactional
    public void resetAutoIncrementId() {
        if (notificationRepository.count() == 0) {
            try {
                entityManager.createNativeQuery("ALTER TABLE notifications AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();
            } catch (Exception e) {
                log.error("❌ Failed to reset notification auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset notification auto-increment: " + e.getMessage());
            }
        }
    }
}
