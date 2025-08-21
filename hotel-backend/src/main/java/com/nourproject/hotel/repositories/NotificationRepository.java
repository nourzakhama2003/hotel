package com.nourproject.hotel.repositories;

import com.nourproject.hotel.entities.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
}
