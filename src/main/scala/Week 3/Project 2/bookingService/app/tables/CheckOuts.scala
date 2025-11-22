package tables

import models.CheckOut
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp

class CheckOuts(tag: Tag) extends Table[CheckOut](tag, "check_outs") {

  def checkOutId = column[String]("check_out_id", O.PrimaryKey)

  def checkInId = column[String]("check_in_id")

  def bookingId = column[Option[String]]("booking_id")

  def roomId = column[String]("room_id")

  def guestId = column[String]("guest_id")

  def actualCheckOutTime = column[Timestamp]("actual_check_out_time")

  def expectedCheckOutTime = column[Option[Timestamp]]("expected_check_out_time")

  def totalRoomCharges = column[BigDecimal]("total_room_charges")

  def additionalCharges = column[Option[BigDecimal]]("additional_charges")

  def discounts = column[Option[BigDecimal]]("discounts")

  def finalAmount = column[BigDecimal]("final_amount")

  def paymentStatus = column[String]("payment_status")

  def paymentMethod = column[Option[String]]("payment_method")

  def handledByStaffId = column[Option[String]]("handled_by_staff_id")

  def remarks = column[Option[String]]("remarks")

  def createdAt = column[Timestamp]("created_at")

  def updatedAt = column[Timestamp]("updated_at")

  // ---------------------------------------------
  // Foreign Keys
  // ---------------------------------------------

  def fkCheckIn = foreignKey(
    "fk_checkout_checkin",
    checkInId,
    TableQuery[CheckIns]
  )(_.checkInId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  def fkBooking = foreignKey(
    "fk_checkout_booking",
    bookingId,
    TableQuery[Bookings]
  )(_.bookingId.?, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.SetNull)

  def fkRoom = foreignKey(
    "fk_checkout_room",
    roomId,
    TableQuery[Rooms]
  )(_.roomId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  def fkGuest = foreignKey(
    "fk_checkout_guest",
    guestId,
    TableQuery[Guests]
  )(_.guestId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  // ---------------------------------------------
  // Case class projection (handles no default values)

  override def * =
    (
      checkOutId,
      checkInId,
      bookingId,
      roomId,
      guestId,
      actualCheckOutTime,
      expectedCheckOutTime,
      totalRoomCharges,
      additionalCharges,
      discounts,
      finalAmount,
      paymentStatus,
      paymentMethod,
      handledByStaffId,
      remarks,
      createdAt,
      updatedAt
    )<> ((CheckOut.apply _).tupled, CheckOut.unapply)

}