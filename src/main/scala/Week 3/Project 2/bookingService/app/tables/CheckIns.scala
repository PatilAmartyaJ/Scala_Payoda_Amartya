package tables

import models.{Address, CheckIn}
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp


class CheckIns(tag: Tag) extends Table[CheckIn](tag, "check_ins") {

  def checkInId = column[String]("check_in_id", O.PrimaryKey)
  def bookingId = column[Option[String]]("booking_id")

  def hotelId = column[String]("hotel_id")
  def roomId = column[String]("room_id")
  def roomTypeId = column[String]("room_type_id")
  def guestId = column[String]("guest_id")

  def actualCheckInTime = column[Timestamp]("actual_check_in_time")
  def expectedCheckInTime = column[Option[Timestamp]]("expected_check_in_time")

  def numberOfGuests = column[Int]("number_of_guests")

  def depositAmount = column[Option[BigDecimal]]("deposit_amount")

  def idDocumentType = column[Option[String]]("id_document_type")
  def idDocumentNumber = column[Option[String]]("id_document_number")

  def remarks = column[Option[String]]("remarks")
  def handledByStaffId = column[Option[String]]("handled_by_staff_id")

  def status=column[String]("status")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  override def * =
    (
      checkInId,
      bookingId,
      hotelId,
      roomId,
      roomTypeId,
      guestId,
      actualCheckInTime,
      expectedCheckInTime,
      numberOfGuests,
      depositAmount,
      idDocumentType,
      idDocumentNumber,
      remarks,
      handledByStaffId,
      status,
      createdAt,
      updatedAt
    ) <> ((CheckIn.apply _).tupled, CheckIn.unapply)
}


