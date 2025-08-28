package com.nourproject.hotel.mappers;

import com.nourproject.hotel.dtos.payment.PaymentDto;
import com.nourproject.hotel.dtos.payment.PaymentUpdateDto;
import com.nourproject.hotel.entities.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(target = "booking", ignore = true) // Handle booking assignment manually in service
    Payment paymentDtoToPayment(PaymentDto paymentDto);
    
    @Mapping(target = "booking", source = "booking") // Include full booking DTO
    PaymentDto paymentToPaymentDto(Payment payment);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "booking", ignore = true) // Don't update booking via DTO
    void updatePaymentUpdateDtoToPayment(PaymentUpdateDto paymentUpdateDto, @MappingTarget Payment payment);
}
