package Repositories
import Models.{BookingStatus, Room, RoomAvailability, User, UserRole}
import Tables.{BookingTable, RoomTable, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomRepository@Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  val rooms = TableQuery[RoomTable]
  val bookings = TableQuery[BookingTable]


  def findAll(): Future[Seq[Room]] = {
    db.run(rooms.filter(_.isAvailable === true).result)
  }

  def findById(id: String): Future[Option[Room]] = {
    db.run(rooms.filter(r => r.room_id === id && r.isAvailable === true).result.headOption)
  }

  def create(room: Room): Future[Room] = {
    val insert = rooms += room
    db.run(insert).map(_ => room)
  }

  def update(room: Room): Future[Int] = {
    db.run(rooms.filter(_.room_id === room.room_id).update(room))
  }

  def findByName(name: String): Future[Option[Room]] = {
    db.run(rooms.filter(_.name === name).result.headOption)
  }

  def findByLocation(location: String): Future[Option[Room]] = {
    db.run(rooms.filter(_.location === location).result.headOption)
  }

  def findAvailableRoomsByLocation(location: String, startTime: Timestamp, endTime: Timestamp): Future[Seq[Room]] = {
    val startMillis = startTime.getTime
    val endMillis = endTime.getTime

    // First find all rooms at the specified location
    val roomsAtLocation = rooms.filter { room =>
      room.location === location &&
        room.isAvailable === true &&
        room.availablility === RoomAvailability.Available.toString
    }

    // Then check which ones are available for the requested time
    db.run(roomsAtLocation.result).flatMap { rooms =>
      Future.sequence(
        rooms.map { room =>
          checkAvailability(room.room_id, new Timestamp(startMillis), new Timestamp(endMillis)).map { isAvailable =>
            if (isAvailable) Some(room) else None
          }
        }
      ).map(_.flatten)
    }
  }

  def checkAvailability(roomId: String, startTime: Timestamp, endTime: Timestamp): Future[Boolean] = {
    val conflictQuery = bookings.filter { booking =>
      booking.roomId === roomId &&
        booking.status =!= BookingStatus.Cancelled.toString &&
        booking.status =!= BookingStatus.Completed.toString &&
        booking.status =!= BookingStatus.Released.toString &&
        ((booking.startTime >= startTime && booking.startTime < endTime) ||
          (booking.endTime > startTime && booking.endTime <= endTime) ||
          (booking.startTime <= startTime && booking.endTime >= endTime))
    }.exists

    db.run(conflictQuery.result).map(!_)

  }

  def updateRoomAvailability(roomId: String, availability: RoomAvailability): Future[Int] = {
    db.run(
      rooms.filter(_.room_id === roomId)
        .map(r => (r.availablility, r.updatedAt))
        .update((availability.toString, new Timestamp(System.currentTimeMillis())))
    )
  }

  def findByCapacity(minCapacity: Int): Future[Seq[Room]] = {
    db.run(rooms.filter(r => r.capacity >= minCapacity && r.isAvailable === true).result)
  }




  def getCurrentlyAvailableRooms(): Future[Seq[Room]] = {
    val now = new Timestamp(System.currentTimeMillis())
    val currentlyBookedRoomIds = bookings.filter { booking =>
      booking.startTime <= now &&
        booking.endTime >= now &&
        (booking.status === BookingStatus.Confirmed.toString || // Use string comparison
          booking.status === BookingStatus.InProgress.toString)
    }.map(_.roomId)

    db.run(
      rooms.filter { room =>
        room.isAvailable === true &&
          room.availablility === RoomAvailability.Available.toString && // Use string comparison
          !(room.room_id in currentlyBookedRoomIds)
      }.result
    )
  }





}
