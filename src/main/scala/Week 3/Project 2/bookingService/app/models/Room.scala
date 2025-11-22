package models

import util.IDGenerator.IdType
import util.IDGenerator

import java.sql.Timestamp


case class Room(
                 roomId: String,
                 hotelId: String,
                 roomNumber: String,
                 roomFloor: Int,
                 roomTypeId: String,
                 isAlreadyBooked: Boolean,
                 guestId: Option[String],
                 createdAt: Timestamp,
                 updatedAt: Timestamp
               )

object Room {
  def create(
              hotelId: String,
              roomNumber: String,
              roomFloor: Int,
              roomTypeId: String,
              isAlreadyBooked: Boolean,
              guestId: Option[String],

            ): Room = {
    val now = new Timestamp(System.currentTimeMillis())
    new Room(
      roomId = IDGenerator.generate(IdType.Room),
      hotelId = hotelId,
      roomNumber = roomNumber,
      roomFloor = roomFloor,
      roomTypeId = roomTypeId,
      isAlreadyBooked = isAlreadyBooked,
      guestId = guestId,
      createdAt = now,
      updatedAt = now
    )

  }
}