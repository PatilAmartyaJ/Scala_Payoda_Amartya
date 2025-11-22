package models

import util.IDGenerator
import util.IDGenerator.IdType

import java.sql.Timestamp
import java.time.Instant
import scala.math.BigDecimal.RoundingMode

case class CheckOut(
                     checkOutId: String, // auto-increment or generated ID
                     checkInId: String, // FK to CheckIn
                     bookingId: Option[String], // FK to Booking / Reservation
                     roomId: String, // FK to Room
                     guestId: String, // FK to Guest
                     actualCheckOutTime: Timestamp, // actual check-out timestamp
                     expectedCheckOutTime: Option[Timestamp], // from booking/reservation
                     totalRoomCharges: BigDecimal,
                     additionalCharges: Option[BigDecimal],
                     discounts: Option[BigDecimal],
                     finalAmount: BigDecimal,
                     paymentStatus: String, // paid, pending, partially_paid
                     paymentMethod: Option[String], // Cash, Card, OTA, etc.
                     handledByStaffId: Option[String], // FK to Staff who processed checkout
                     remarks: Option[String] = None, // late checkout, damage fees, etc.
                     createdAt: Timestamp,
                     updatedAt: Timestamp

                   )

object CheckOut {
  def create(
              checkInId: String, // FK to CheckIn
              bookingId: Option[String], // FK to Booking / Reservation
              roomId: String, // FK to Room
              guestId: String, // FK to Guest
              actualCheckOutTime: Timestamp, // actual check-out timestamp
              expectedCheckOutTime: Option[Timestamp], // from booking/reservation
              totalRoomCharges: BigDecimal,
              additionalCharges: Option[BigDecimal],
              discounts: Option[BigDecimal],
              finalAmount: BigDecimal,
              paymentStatus: String, // paid, pending, partially_paid
              paymentMethod: Option[String], // Cash, Card, OTA, etc.
              handledByStaffId: Option[String], // FK to Staff who processed checkout
              remarks: Option[String] = None, // late checkout, damage fees, etc.
            ): CheckOut = {
    val now = new Timestamp(System.currentTimeMillis())
    new CheckOut(
      checkOutId = IDGenerator.generate(IdType.CheckOut),
      checkInId = checkInId, // FK to CheckIn
      bookingId = bookingId, // FK to Booking / Reservation
      roomId = roomId, // FK to Room
      guestId = guestId, // FK to Guest
      actualCheckOutTime = actualCheckOutTime, // actual check-out timestamp
      expectedCheckOutTime = expectedCheckOutTime, // from booking/reservation
      totalRoomCharges = totalRoomCharges,
      additionalCharges = additionalCharges,
      discounts = discounts,
      finalAmount = finalAmount,
      paymentStatus = paymentStatus, // paid, pending, partially_paid
      paymentMethod = paymentMethod, // Cash, Card, OTA, etc.
      handledByStaffId = handledByStaffId, // FK to Staff who processed checkout
      remarks = remarks, // late checkout, damage fees, etc.
      createdAt = now,
      updatedAt = now

    )
  }
}
