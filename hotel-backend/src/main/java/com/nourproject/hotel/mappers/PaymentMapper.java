package com.nourproject.hotel.mappers;


import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.entities.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(target = "booking", ignore = true) // Handle booking assignment manually in service
    @Mapping(target = "payementStatus", ignore = true) // Payment status handled in service layer
    @Mapping(target = "Id", ignore = true) // ID is auto-generated, don't set from DTO
    Payment paymentDtoToPayment(PaymentDto paymentDto);
    
    @Mapping(target = "bookingId", source = "booking.id") // Map booking.id to bookingId
    @Mapping(target = "approvalLink", ignore = true) // approvalLink not in Payment entity, set manually when needed
    @Mapping(target = "Id", ignore = true) // Ignore Id mapping issue
    PaymentDto paymentToPaymentDto(Payment payment);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "booking", ignore = true) // Don't update booking via DTO  
    @Mapping(target = "payementStatus", ignore = true) // Payment status handled in service
    @Mapping(target = "id", ignore = true) // Don't update ID
    @Mapping(target = "paymentDate", ignore = true) // Payment date managed by system
    @Mapping(target = "success", ignore = true) // Success status managed by business logic
    void updatePaymentUpdateDtoToPayment(PaymentUpdateDto paymentUpdateDto, @MappingTarget Payment payment);
}
