package com.nourproject.hotel.controllers;

import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.services.PaymentServiceCrud;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payment/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentServiceCrud paymentService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Response> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getPaymentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Response> getPaymentByTransactionId(@PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(paymentService.findPaymentByTransactionId(transactionId));
    }

    @GetMapping("/booking-reference/{bookingReference}")
    public ResponseEntity<Response> getPaymentsByBookingReference(@PathVariable("bookingReference") String bookingReference) {
        return ResponseEntity.ok(paymentService.findPaymentsByBookingReference(bookingReference));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createPayment(
            @Valid @RequestBody PaymentDto paymentDto,
            @RequestParam("bookingId") Long bookingId) {
        return ResponseEntity.ok(paymentService.savePayment(paymentDto, bookingId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updatePayment(
            @PathVariable("id") Long id,
            @Valid @RequestBody PaymentUpdateDto paymentUpdateDto) {
        return ResponseEntity.ok(paymentService.updatePaymentById(id, paymentUpdateDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deletePayment(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.deleteById(id));
    }
}
