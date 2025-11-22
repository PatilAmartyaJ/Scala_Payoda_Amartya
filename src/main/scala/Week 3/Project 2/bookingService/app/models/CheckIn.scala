package models

import util.IDGenerator
import util.IDGenerator.IdType

import java.sql.Timestamp


case class CheckIn(
                    checkInId: String,
                    bookingId: Option[String],
                    hotelId:String,
                    roomId: String,
                    roomTypeId:String,
                    guestId: String,
                    actualCheckInTime: Timestamp,
                    expectedCheckInTime: Option[Timestamp],
                    numberOfGuests: Int,
                    depositAmount: Option[BigDecimal],
                    idDocumentType: Option[String],
                    idDocumentNumber: Option[String],
                    remarks: Option[String],
                    handledByStaffId:Option[String],
                    status:String,
                    createdAt: Timestamp,
                    updatedAt: Timestamp
                  )

object CheckIn {
  def create(
              bookingId: Option[String],
              hotelId:String,
              roomId: String,
              roomTypeId:String,
              guestId: String,
              actualCheckInTime: Timestamp,
              expectedCheckInTime: Option[Timestamp],
              numberOfGuests: Int,
              depositAmount: Option[BigDecimal],
              idDocumentType: Option[String],
              idDocumentNumber: Option[String],
              remarks: Option[String],
              handledByStaffId:Option[String],
              status:String
            ): CheckIn = {
    val now = new Timestamp(System.currentTimeMillis())
    new CheckIn(
      checkInId=IDGenerator.generate(IdType.CheckIn),
      bookingId = bookingId,
      hotelId = hotelId,
      roomId=roomId,
      roomTypeId=roomTypeId,
      guestId=guestId,
      actualCheckInTime=actualCheckInTime,
      expectedCheckInTime=expectedCheckInTime,
      numberOfGuests=numberOfGuests,
      depositAmount=depositAmount,
      idDocumentType=idDocumentType,
      idDocumentNumber=idDocumentNumber,
      remarks=remarks,
      handledByStaffId=handledByStaffId,
      status=status,
      createdAt=now,
      updatedAt=now
    )
  }
}