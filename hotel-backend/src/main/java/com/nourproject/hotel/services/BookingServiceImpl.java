package com.nourproject.hotel.services;
import com.nourproject.hotel.dtos.notification.NotificationDto;
import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.dtos.booking.BookingUpdateDto;
import com.nourproject.hotel.dtos.Response;
import com.nourproject.hotel.dtos.booking.BookingDto;
import com.nourproject.hotel.entities.*;
import com.nourproject.hotel.enums.BookingStatus;
import com.nourproject.hotel.exceptions.InvalidBookingStateOrDate;
import com.nourproject.hotel.exceptions.NotFoundException;
import com.nourproject.hotel.mappers.BookingMapper;
import com.nourproject.hotel.notifications.NotificationService;
import com.nourproject.hotel.repositories.*;
import com.nourproject.hotel.services.BookingReferenceGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.nourproject.hotel.exceptions.GlobalException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements com.nourproject.hotel.services.interfaces.BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
   private final NotificationService notificationService;

private final BookingReferenceGenerator bookingReferenceGenerator;
    @PersistenceContext
    private EntityManager entityManager;

    public Response findAllBookings(){
        List<BookingDto> bookingList = this.bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(bookingMapper::bookingToBookingDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("booking retrieved successfully")
                .bookings(bookingList)
                .build();
    }

    public Response findbookingByUserId(Long id){
        List<BookingDto> bookingdto = this.bookingRepository.findByUserId(id)
                .stream()
                .map(bookingMapper::bookingToBookingDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("bookings for user with id :"+ id +" , retrieved successfully")
                .bookings(bookingdto)
                .build();
    }
    public Response findbookingBookingRefrence(String refrence){
        List<BookingDto> bookingdto = this.bookingRepository.findByBookingRefrence(refrence)
                .stream()
                .map(bookingMapper::bookingToBookingDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("bookings with  refrence :"+ refrence +", retrieved successfully")
                .bookings(bookingdto)
                .build();


    }

    public Boolean isRoomAvailableForBooking(Long roomId,LocalDateTime checkInDate,LocalDateTime checkOutDate){
        return this.bookingRepository.isRoomAvailableForBooking(roomId,checkInDate,checkOutDate);
    }
    public Response findbookingById(Long id){
        BookingDto bookingdto = this.bookingRepository.findById(id)
                .map(bookingMapper::bookingToBookingDto)
                .orElseThrow(() -> new GlobalException("booking with ID " + id + ",  not found"));
        return Response.builder()
                .status(200)
                .message("booking retrieved successfully")
                .booking(bookingdto)
                .build();
    }

    public Response saveBooking(BookingDto bookingDto,Long userId,Long roomId){
        if(bookingDto.getCheckInDate().isBefore(LocalDateTime.now())){
            throw new InvalidBookingStateOrDate("checkInDate :"+bookingDto.getCheckInDate()+"must be after now"+LocalDateTime.now());
        }

        if(bookingDto.getCheckInDate().isAfter(bookingDto.getCheckOutDate())){
            throw new InvalidBookingStateOrDate("checkInDate "+bookingDto.getCheckInDate()+" must be before checkOutDate"+bookingDto.getCheckOutDate());
        }
        if(bookingDto.getCheckInDate().isEqual(bookingDto.getCheckOutDate())){
            throw new InvalidBookingStateOrDate("checkInDate :"+bookingDto.getCheckInDate()+"must be before checkOutDate"+bookingDto.getCheckOutDate());
        }
        Boolean isAvailable=this.isRoomAvailableForBooking(roomId,bookingDto.getCheckInDate(),bookingDto.getCheckOutDate());
        if(!isAvailable){
            throw new InvalidBookingStateOrDate("room is already booked in this period. please choose another room or change the booking period");
        }






        User user=this.userRepository.findById(userId).orElseThrow(()->new NotFoundException("user with ID "+userId+" not found"));
        Room room=this.roomRepository.findById(roomId).orElseThrow(()->new NotFoundException("room with ID "+roomId+" not found"));
      //  Payment payment=this.paymentRepository.findById(paymentId).orElseThrow(()->new NotFoundException("payment with ID "+paymentId+" not found"));
        BigDecimal totalPrice=calculateTotalPrice(bookingDto,room);
         String bookingRefrence=bookingReferenceGenerator.generateBookingReference();
        Booking booking=this.bookingMapper.bookingDtoToBooking(bookingDto);
        user.setIsActive(true);
        booking.setBookingRefrence(bookingRefrence);
        booking.setTotalPrice(totalPrice);
        booking.setUser(user);
        booking.setRoom(room);
       // booking.setPayment(payment);
        booking.setBookingStatus(BookingStatus.BOOKED);
        Booking  savedBooking = this.bookingRepository.save(booking);
String paymentLink="http://localhost:4200/payment/"+bookingRefrence+"/"+totalPrice;
log.info("payment link is :"+paymentLink);
      NotificationDto notificationDto=NotificationDto.builder()
              .recipient(user.getEmail())
              .subject("room number :"+room.getRoomNumber()+" Booking Confirmation")
              .body("hotel booking app, Your booking has been confirmed for the periode from  "+ bookingDto.getCheckInDate()+ " to "+bookingDto.getCheckOutDate()+"your booking refrence : "+bookingRefrence+" " +
                      " You can proced your payements at "+paymentLink)
              .bookingReference(bookingRefrence)
              .build();

        notificationService.sendEmail(notificationDto);

        return Response.builder()
                .status(201)
                .message("booking saved successfully")
                .booking(bookingMapper.bookingToBookingDto(savedBooking))
                .build();
    }

    private BigDecimal calculateTotalPrice(BookingDto bookingDto,Room room){
BigDecimal pricePerNight=room.getPricePerNight();
Long days= ChronoUnit.DAYS.between(bookingDto.getCheckInDate(),bookingDto.getCheckOutDate());
if(days<1){
    throw new InvalidBookingStateOrDate("booking should be at least for  one day");
}
 return pricePerNight.multiply(BigDecimal.valueOf(days));

    }

    public Response updateBookingById(Long id, BookingUpdateDto bookingUpdateDto){
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(() -> new GlobalException("booking with ID " + id + " not found"));
        this.bookingMapper.updateBookingUpdateDtoToBooking(bookingUpdateDto, booking);
        Booking savedBooking = this.bookingRepository.save(booking);
        return Response.builder()
                .status(200)
                .message("booking updated successfully")
                .booking(bookingMapper.bookingToBookingDto(savedBooking))
                .build();
    }
    public Response findByUserUserName(String userName){
        List<BookingDto> bookings=bookingRepository.findByUserUserName(userName).stream().map(bookingMapper::bookingToBookingDto).toList();
       return Response.builder()
               .status(200)
               .message("bookings for user "+userName+"retreived successfully")
               .bookings(bookings)
               .build();
    }



    @Transactional
    public Response deleteById(Long id){
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(() -> new GlobalException("booking with ID " + id + " not found"));

        BookingDto deletedbookingDto = bookingMapper.bookingToBookingDto(booking);
        this.bookingRepository.delete(booking);
        this.bookingRepository.flush();
        resetAutoIncrementId();

        return Response.builder()
                .status(200)
                .message("booking deleted successfully")
                .booking(deletedbookingDto)
                .build();
    }


    @Transactional
    public void resetAutoIncrementId(){
        if(this.bookingRepository.count() == 0) {
            try {
                // Use the injected entityManager instance, not the class
                entityManager.createNativeQuery("ALTER TABLE hotels AUTO_INCREMENT = 1").executeUpdate();
                entityManager.flush();

            } catch (Exception e) {
                System.err.println("❌ Failed to reset room auto-increment: " + e.getMessage());
                throw new GlobalException("Failed to reset room auto-increment: " + e.getMessage());
            }
        }
    }




}
