package models

import util.IDGenerator.IdType
import util.IDGenerator

import java.sql.Timestamp
import java.time.LocalDate

case class Booking(
                    bookingId: String,
                    hotelId: String,
                    roomId: String,
                    guestId: String,
                    checkInDate: LocalDate,
                    checkOutDate: LocalDate,
                    bookingStatus: String,
                    numberOfGuests: Int,
                    ratePerNight: BigDecimal,
                    totalAmount: Option[BigDecimal],
                    paymentStatus: String,
                    paymentMethod: Option[String],
                    specialRequests: Option[String],
                    promoCode: Option[String],
                    createdAt: Timestamp,
                    updatedAt: Timestamp
                  )

object Booking {
  def create(
             hotelId: String,
             roomId: String,
             guestId: String,
             checkInDate: LocalDate,
             checkOutDate: LocalDate,
             bookingStatus: String,
             numberOfGuests: Int,
             ratePerNight: BigDecimal,
             totalAmount: Option[BigDecimal],
             paymentStatus: String,
             paymentMethod: Option[String],
             specialRequests: Option[String],
             promoCode: Option[String],

           ): Booking = {
    val now = new Timestamp(System.currentTimeMillis())
    new Booking(
      bookingId = IDGenerator.generate(IdType.Booking),
      hotelId = hotelId,
      roomId=roomId,
      guestId=guestId,
      checkInDate=checkInDate,
      checkOutDate=checkOutDate,
      bookingStatus=bookingStatus,
      numberOfGuests=numberOfGuests,
      ratePerNight=ratePerNight,
      totalAmount=totalAmount,
      paymentStatus=paymentStatus,
      paymentMethod=paymentMethod,
      specialRequests=specialRequests,
      promoCode=promoCode,
      createdAt=now,
      updatedAt=now
    )
  }
}