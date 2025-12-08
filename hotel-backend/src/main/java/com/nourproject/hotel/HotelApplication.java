package com.nourproject.hotel;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.notifications.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class HotelApplication {
//private final NotificationService notificationService;
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }



//    @PostConstruct
//    public void  mail(){
//        NotificationDto notificationDto=NotificationDto.builder()
//                .recipient("nourzakhama2003@gmail.com")
//                .subject("Test")
//                .body("Test mail sending")
//                .build();
//        notificationService.sendEmail(notificationDto);
//
//    }

}
