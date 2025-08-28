package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;
import com.nourproject.hotel.dtos.Response;

import com.nourproject.hotel.services.interfaces.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/public/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Response> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getBookingById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookingService.findbookingById(id));
    }
    @GetMapping("/userName/{userName}")
    public ResponseEntity<Response> findByUserUsername(@PathVariable("userName") String userName) {
        return ResponseEntity.ok(bookingService.findByUserUserName(userName));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getBookingsByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(bookingService.findbookingByUserId(userId));
    }

    @GetMapping("/reference")
    public ResponseEntity<Response> getBookingByReference(@RequestParam("ref") String reference) {
        return ResponseEntity.ok(bookingService.findbookingBookingRefrence(reference));
    }

    @GetMapping("/availability/{roomId}")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @PathVariable("roomId") Long roomId,
            @RequestParam("checkIn") LocalDateTime checkInDate,
            @RequestParam("checkOut") LocalDateTime checkOutDate) {
        return ResponseEntity.ok(bookingService.isRoomAvailableForBooking(roomId, checkInDate, checkOutDate));
    }

    @PostMapping()
    public ResponseEntity<Response> createBooking(
            @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingDto, bookingDto.getUserId(), bookingDto.getRoomId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateBooking(
            @PathVariable("id") Long id,
            @Valid @RequestBody BookingUpdateDto bookingUpdateDto) {
        return ResponseEntity.ok(bookingService.updateBookingById(id, bookingUpdateDto));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteBooking(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookingService.deleteById(id));
    }
}
