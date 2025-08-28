package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.entities.Booking;
import com.nourproject.hotel.entities.Payment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T23:32:01+0100",
    comments = "version: 1.6.1, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment paymentDtoToPayment(PaymentDto paymentDto) {
        if ( paymentDto == null ) {
            return null;
        }

        Payment.PaymentBuilder payment = Payment.builder();

        payment.transactionId( paymentDto.getTransactionId() );
        payment.amount( paymentDto.getAmount() );
        payment.bookingRefrence( paymentDto.getBookingRefrence() );
        payment.failueReason( paymentDto.getFailueReason() );
        payment.paymentGateway( paymentDto.getPaymentGateway() );

        return payment.build();
    }

    @Override
    public PaymentDto paymentToPaymentDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentDto.PaymentDtoBuilder paymentDto = PaymentDto.builder();

        paymentDto.booking( bookingToBookingDto( payment.getBooking() ) );
        paymentDto.transactionId( payment.getTransactionId() );
        paymentDto.amount( payment.getAmount() );
        paymentDto.paymentDate( payment.getPaymentDate() );
        paymentDto.bookingRefrence( payment.getBookingRefrence() );
        paymentDto.failueReason( payment.getFailueReason() );
        paymentDto.paymentGateway( payment.getPaymentGateway() );

        return paymentDto.build();
    }

    @Override
    public void updatePaymentUpdateDtoToPayment(PaymentUpdateDto paymentUpdateDto, Payment payment) {
        if ( paymentUpdateDto == null ) {
            return;
        }
    }

    protected BookingDto bookingToBookingDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDto.BookingDtoBuilder bookingDto = BookingDto.builder();

        bookingDto.id( booking.getId() );
        bookingDto.bookingStatus( booking.getBookingStatus() );
        bookingDto.checkInDate( booking.getCheckInDate() );
        bookingDto.checkOutDate( booking.getCheckOutDate() );
        bookingDto.bookingRefrence( booking.getBookingRefrence() );
        bookingDto.totalPrice( booking.getTotalPrice() );
        bookingDto.payementStatus( booking.getPayementStatus() );

        return bookingDto.build();
    }
}
