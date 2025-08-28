package com.nourproject.hotel.services.interfaces;

import com.nourproject.hotel.dtos.*;
import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;

public interface PaymentServiceCrud {
    Response findAllPayments();
    Response findPaymentById(Long id);
    Response findPaymentByTransactionId(String transactionId);
    Response findPaymentsByBookingReference(String bookingReference);
    Response savePayment(PaymentDto paymentDto, Long bookingId);
    Response updatePaymentById(Long id, PaymentUpdateDto paymentUpdateDto);
    Response deleteById(Long id);
}
