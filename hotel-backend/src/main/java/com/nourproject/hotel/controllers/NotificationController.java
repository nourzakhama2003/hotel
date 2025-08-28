package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.notification.NotificationUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.enums.NotificationType;
import com.nourproject.hotel.services.NotificationServiceCrud;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/notification/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationServiceCrud notificationServiceCrud;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Response> getAllNotifications() {
        return ResponseEntity.ok(notificationServiceCrud.findAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getNotificationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationServiceCrud.findNotificationById(id));
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<Response> getNotificationsByRecipient(@PathVariable("recipient") String recipient) {
        return ResponseEntity.ok(notificationServiceCrud.findNotificationsByRecipient(recipient));
    }

    @GetMapping("/booking-reference/{bookingReference}")
    public ResponseEntity<Response> getNotificationsByBookingReference(@PathVariable("bookingReference") String bookingReference) {
        return ResponseEntity.ok(notificationServiceCrud.findNotificationsByBookingReference(bookingReference));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Response> getNotificationsByType(@PathVariable("type") NotificationType type) {
        return ResponseEntity.ok(notificationServiceCrud.findNotificationsByType(type));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationServiceCrud.saveNotification(notificationDto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Response> updateNotification(
            @PathVariable("id") Long id,
            @Valid @RequestBody NotificationUpdateDto notificationUpdateDto) {
        return ResponseEntity.ok(notificationServiceCrud.updateNotificationById(id, notificationUpdateDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteNotification(@PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationServiceCrud.deleteById(id));
    }
}
