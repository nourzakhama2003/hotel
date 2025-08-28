package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.notification.NotificationUpdateDto;
import com.nourproject.hotel.entities.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public Notification notificationDtoToNotification(NotificationDto notificationDto) {
        if ( notificationDto == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.id( notificationDto.getId() );
        notification.subject( notificationDto.getSubject() );
        notification.recipient( notificationDto.getRecipient() );
        notification.body( notificationDto.getBody() );
        notification.type( notificationDto.getType() );
        notification.bookingReference( notificationDto.getBookingReference() );

        return notification.build();
    }

    @Override
    public NotificationDto notificationToNotificationDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto.NotificationDtoBuilder notificationDto = NotificationDto.builder();

        notificationDto.id( notification.getId() );
        notificationDto.subject( notification.getSubject() );
        notificationDto.recipient( notification.getRecipient() );
        notificationDto.body( notification.getBody() );
        notificationDto.type( notification.getType() );
        notificationDto.bookingReference( notification.getBookingReference() );
        notificationDto.createdAt( notification.getCreatedAt() );

        return notificationDto.build();
    }

    @Override
    public void updateNotificationUpdateDtoToNotification(NotificationUpdateDto notificationUpdateDto, Notification notification) {
        if ( notificationUpdateDto == null ) {
            return;
        }
    }
}
