package mapper

import Summary.BookingSummary
import models.Booking

import java.time.LocalDate

object BookingMapper {
   def summary(booking:Booking):BookingSummary = {
     BookingSummary(
       booking_Id=booking.bookingId,
       hotel_Id=booking.hotelId,
       room_Id=booking.roomId,
       guest_Id=booking.guestId,
       check_In_Date=booking.checkInDate,
       check_Out_Date=booking.checkOutDate,
       booking_Status=booking.bookingStatus,
       number_Of_Guests=booking.numberOfGuests,
       rate_Per_Night=booking.ratePerNight,
       total_Amount=booking.totalAmount,
       payment_Status=booking.paymentStatus,
       payment_Method=booking.paymentMethod,
     )
   }
}
