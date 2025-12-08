package com.nourproject.hotel.services;


import com.nourproject.hotel.entities.BookingReference;
import com.nourproject.hotel.repositories.BookingRepository;
import com.nourproject.hotel.repositories.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookingReferenceGenerator {

    private final BookingRepository bookingRepository;
    private final BookingReferenceRepository bookingReferenceRepository;


    public String generateBookingReference(){
        String bookingReference;

        // keep generating until a unique code is found
        do{
            bookingReference = generateRandomAlphaNumericCode(10); //genrate code of length 10

        }while (isBookingReferenceExistInBookings(bookingReference)); //check if the code already exist in bookings table

        // Don't save to separate booking_reference table - the reference will be saved with the booking
        return bookingReference;
    }


    private String generateRandomAlphaNumericCode(int length){

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++){
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    private boolean isBookingReferenceExistInBookings(String bookingReference){
        return bookingRepository.findByBookingReference(bookingReference).isPresent();
    }

    // Method to save booking reference to audit table after booking is successfully created
    public void saveBookingReferenceToAuditTable(String bookingReference) {
        try {
            // Check if already exists in audit table to avoid duplicates
            if (!bookingReferenceRepository.findByReferenceNo(bookingReference).isPresent()) {
                BookingReference newBookingReference = BookingReference.builder()
                    .referenceNo(bookingReference)
                    .build();
                bookingReferenceRepository.save(newBookingReference);
            }
        } catch (Exception e) {
            // Log the error but don't fail the booking process
            System.err.println("Failed to save booking reference to audit table: " + e.getMessage());
        }
    }
}
