package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.notification.NotificationUpdateDto;
import com.nourproject.hotel.entities.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    Notification notificationDtoToNotification(NotificationDto notificationDto);
    
    NotificationDto notificationToNotificationDto(Notification notification);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationUpdateDtoToNotification(NotificationUpdateDto notificationUpdateDto, @MappingTarget Notification notification);
}
