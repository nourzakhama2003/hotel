package com.nourproject.hotel.payments.stripe;

import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.entities.Payment;
import com.nourproject.hotel.enums.NotificationType;
import com.nourproject.hotel.enums.PaymentGateway;
import com.nourproject.hotel.enums.PaymentStatus;
import com.nourproject.hotel.exceptions.GlobalException;
import com.nourproject.hotel.exceptions.NotFoundException;
import com.nourproject.hotel.mappers.PaymentMapper;
import com.nourproject.hotel.notifications.NotificationService;
import com.nourproject.hotel.repositories.BookingRepository;
import com.nourproject.hotel.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    @Value("${stripe.api.secret.key}")
    private String secretKey;

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
        List<PaymentDto> paymentList = paymentRepository.findByBookingReference(bookingReference)
                .stream()
                .map(paymentMapper::paymentToPaymentDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Payments retrieved successfully")
                .payments(paymentList)
                .build();
    }

    public Response initializePayment(PaymentDto paymentDto) {

        Stripe.apiKey = secretKey;
String bookingReference = paymentDto.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking with reference : " + bookingReference + " not found"));
if(booking.getPaymentStatus()== PaymentStatus.COMPLETED){
    throw new GlobalException("Payment already made for this booking");
}
if(booking.getTotalPrice().compareTo(paymentDto.getAmount())!=0){
    throw new GlobalException("Total price doesn't match,contact our custommer support ");

}
try{
    PaymentIntentCreateParams params=PaymentIntentCreateParams.builder()
            .setAmount(paymentDto.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
            .setCurrency("eur") // Changed to euro
            .putMetadata("bookingReference", bookingReference)
            .build();
    PaymentIntent paymentIntent = PaymentIntent.create(params);
    String transactionId= paymentIntent.getClientSecret();

    return Response.builder()
            .status(200)
            .message("Payment made successfully")
            .transactionId(transactionId)
            .build();

}catch(StripeException e){
    throw new RuntimeException("error while creating payment unique transaction id :"+e.getMessage());
}





    }
public Response createPayment(PaymentDto paymentDto ){
        String bookingReference = paymentDto.getBookingReference();
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking with reference : " + bookingReference + " not found"));
    Payment payment = paymentMapper.paymentDtoToPayment(paymentDto);
    payment.setBooking(booking);
    payment.setPaymentGateway(PaymentGateway.STRIPE);
    if(payment.getTransactionId()==null){
        throw new GlobalException("Transaction id is null in payment,please try again or contact our customer support");
    }
    payment.setTransactionId(paymentDto.getTransactionId() );
payment.setPayementStatus(paymentDto.isSuccess()? PaymentStatus.COMPLETED: PaymentStatus.FAILED);
   payment.setPaymentDate(LocalDateTime.now());
   payment.setBookingReference(bookingReference);


if(!payment.isSuccess()){
    payment.setFailueReason(paymentDto.getFailueReason() );
}
    Payment savedPayment = paymentRepository.save(payment);

    NotificationDto notificationDto=NotificationDto.builder()
            .recipient(booking.getUser().getEmail())
            .type(NotificationType.EMAIL)
            .bookingReference(bookingReference)
            .build();

    if(paymentDto.isSuccess()){
        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRepository.save(booking);
    notificationDto.setSubject("BOOKING PAYEMENT SUCCESSFUL");
    notificationDto.setBody("Your booking payment has been successful with refrence : "+bookingReference);
    notificationService.sendEmail(notificationDto);
    }else{
        booking.setPaymentStatus(PaymentStatus.FAILED);
        bookingRepository.save(booking);
        notificationDto.setSubject("BOOKING PAYEMENT FAILED");
        notificationDto.setBody("Your booking payment has been FAILED with refrence : "+bookingReference+" ,because of ;"+ paymentDto.getFailueReason()+",please contact our custommer support for more details");
        notificationService.sendEmail(notificationDto);
    }

    return Response.builder()
            .status(200)
            .message(paymentDto.isSuccess() ? "Payment processed successfully" : "Payment failed but recorded")
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
