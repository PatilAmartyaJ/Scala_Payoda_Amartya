package models
import util.IDGenerator.IdType
import util.IDGenerator

import java.sql.Timestamp
import java.time.Instant

case class Hotel(
                  hotelId: String,
                  hotelName:String,
                  hotelAddressId: String,
                  assignedOwnerId: Option[String]=None,
                  totalFloors: Int,
                  totalRooms: Int,
                  createdAt: Timestamp,
                  updatedAt: Timestamp,
                )

object Hotel {
  def create(
             hotelName: String,
             hotelAddressId: String,
             assignedOwnerid: Option[String],
             totalFloors: Int,
             totalRooms: Int
           ): Hotel = {
    val now = new Timestamp(System.currentTimeMillis())
    new Hotel(
      hotelId = IDGenerator.generate(IdType.Hotel),
      hotelName=hotelName,
      hotelAddressId = hotelAddressId,
      assignedOwnerId = assignedOwnerid,
      totalFloors = totalFloors,
      totalRooms = totalRooms,
      createdAt = now,
      updatedAt = now
    )

  }
}

