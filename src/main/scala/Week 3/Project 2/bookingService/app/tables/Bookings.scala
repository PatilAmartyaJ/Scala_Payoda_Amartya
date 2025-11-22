
package tables

import models.Booking
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import java.time.LocalDate

class Bookings(tag: Tag) extends Table[Booking](tag, "booking") {

  def bookingId = column[String]("booking_id", O.PrimaryKey)

  def hotelId = column[String]("hotel_id")
  def roomId = column[String]("room_id")
  def guestId = column[String]("guest_id")

  def checkInDate = column[LocalDate]("check_in_date")
  def checkOutDate = column[LocalDate]("check_out_date")

  def bookingStatus = column[String]("booking_status")

  def numberOfGuests = column[Int]("number_of_guests")
  def ratePerNight = column[BigDecimal]("rate_per_night")
  def totalAmount = column[Option[BigDecimal]]("total_amount")

  def paymentStatus = column[String]("payment_status")
  def paymentMethod = column[Option[String]]("payment_method")

  def specialRequests = column[Option[String]]("special_requests")
  def promoCode = column[Option[String]]("promo_code")

  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  override def * = (
    bookingId,
    hotelId,
    roomId,
    guestId,
    checkInDate,
    checkOutDate,
    bookingStatus,
    numberOfGuests,
    ratePerNight,
    totalAmount,
    paymentStatus,
    paymentMethod,
    specialRequests,
    promoCode,
    createdAt,
    updatedAt
  ) <> ((Booking.apply _).tupled, Booking.unapply)
}

