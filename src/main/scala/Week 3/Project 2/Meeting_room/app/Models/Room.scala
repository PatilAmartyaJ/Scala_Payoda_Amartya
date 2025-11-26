// app/models/Room.scala
package Models

import Util.IDGenerator
import Util.IDGenerator.IdType
import java.sql.Timestamp
import java.time.LocalDateTime

case class Room(
                 room_id: String,
                 name: String,
                 location: String,
                 capacity: Int,
                 facilities: Seq[String],
                 isAvailable: Boolean,
                 availability: String,
                 maintenanceSchedule_ID: Option[String],
                 createdAt: Timestamp,
                 updatedAt: Timestamp,
               )

object Room {
  def create(name: String,
             location: String,
             capacity: Int,
             facilities: Seq[String]): Room = {
    val now=new Timestamp(System.currentTimeMillis())
    Room(
      room_id = IDGenerator.generate(IdType.Room),
      name = name,
      location = location,
      capacity = capacity,
      facilities = facilities,
      isAvailable = true,
      availability = "AVAILABLE",
      maintenanceSchedule_ID = None,
      createdAt = now,
      updatedAt = now
    )
  }
}

case class MaintenanceSchedule(
                                maintenance_id: String ,
                                roomId: String, // Foreign key to Room
                                startTime: LocalDateTime,
                                endTime: LocalDateTime,
                                reason: String,
                                maintainedBy: String,
                                createdAt: LocalDateTime = LocalDateTime.now()
                              )

sealed trait RoomFacility {
  def toString: String
}

object RoomFacility {
  case object Projector extends RoomFacility {
    override def toString: String = "PROJECTOR"
  }

  case object Whiteboard extends RoomFacility {
    override def toString: String = "WHITEBOARD"
  }

  case object VideoConferencing extends RoomFacility {
    override def toString: String = "VIDEOCONFERENCING"
  }

  case object AudioSystem extends RoomFacility {
    override def toString: String = "AUDIOSYSTEM"
  }
}

sealed trait RoomAvailability {
  def toString: String

}

object RoomAvailability {
  case object Available extends RoomAvailability {
    override def toString: String = "AVAILABLE"
  }

  case object UnderMaintenance extends RoomAvailability {
    override def toString: String = "UNDERMAINTENANCE"
  }

  case object Reserved extends RoomAvailability {
    override def toString: String = "RESERVED"
  }
}