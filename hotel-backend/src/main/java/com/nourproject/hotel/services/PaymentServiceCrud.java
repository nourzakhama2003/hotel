package com.nourproject.hotel.services;

import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.entities.Payment;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.exceptions.NotFoundException;
import com.nourproject.hotel.mappers.PaymentMapper;
import com.nourproject.hotel.repositories.BookingRepository;
import com.nourproject.hotel.repositories.PaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceCrud {
    
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Response findAllPayments() {
        List<PaymentDto> paymentList = paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(paymentMapper::paymentToPaymentDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Payments retrieved successfully")
                .payments(paymentList)
                .build();
    }

    public Response findPaymentById(Long id) {
        PaymentDto paymentDto = paymentRepository.findById(id)
                .map(paymentMapper::paymentToPaymentDto)
                .orElseThrow(() -> new NotFoundException("Payment with ID " + id + " not found"));
        return Response.builder()
                .status(200)
                .message("Payment retrieved successfully")
                .payment(paymentDto)
                .build();
    }

    public Response findPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new NotFoundException("Payment with transaction ID " + transactionId + " not found"));
        PaymentDto paymentDto = paymentMapper.paymentToPaymentDto(payment);
        return Response.builder()
                .status(200)
                .message("Payment retrieved successfully")
                .payment(paymentDto)
                .build();
    }

    public Response findPaymentsByBookingReference(String bookingReference) {
        List<PaymentDto> paymentList = paymentRepository.findByBookingRefrence(bookingReference)
                .stream()
                .map(paymentMapper::paymentToPaymentDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Payments retrieved successfully")
                .payments(paymentList)
                .build();
    }

    public Response savePayment(PaymentDto paymentDto, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        
        Payment payment = paymentMapper.paymentDtoToPayment(paymentDto);
        payment.setBooking(booking);
        Payment savedPayment = paymentRepository.save(payment);

        return Response.builder()
                .status(201)
                .message("Payment saved successfully")
                .payment(paymentMapper.paymentToPaymentDto(savedPayment))
                .build();
    }

    public Response updatePaymentById(Long id, PaymentUpdateDto paymentUpdateDto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment with ID " + id + " not found"));
        
        paymentMapper.updatePaymentUpdateDtoToPayment(paymentUpdateDto, payment);
        Payment savedPayment = paymentRepository.save(payment);
        
        return Response.builder()
                .status(200)
                .message("Payment updated successfully")
                .payment(paymentMapper.paymentToPaymentDto(savedPayment))
                .build();
    }

    @Transactional
    public Response deleteById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment with ID " + id + " not found"));

        PaymentDto deletedPaymentDto = paymentMapper.paymentToPaymentDto(payment);
        paymentRepository.delete(payment);
        paymentRepository.flush();
        resetAutoIncrementId();

        return Response.builder()
                .status(200)
                .message("Payment deleted successfully")
                .payment(deletedPaymentDto)
                .build();
    }

    @Transactional
    public void resetAutoIncrementId() {
        if (paymentRepository.count() == 0) {
            try {
                entityManager.createNativeQuery("ALTER TABLE payments AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();
            } catch (Exception e) {
                log.error("❌ Failed to reset payment auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset payment auto-increment: " + e.getMessage());
            }
        }
    }
}
