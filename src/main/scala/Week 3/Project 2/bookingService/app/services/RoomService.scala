package services

import models.{Room, RoomType}
import repositories.{RoomRepository, RoomTypeRepository}
import util.IDGenerator
import util.IDGenerator.IdType

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomService @Inject()(roomRepo: RoomRepository, roomTypeRepo: RoomTypeRepository)(implicit ec: ExecutionContext) {


  def createRoomInHotel(hotelId: String, roomNumber: String, roomType: String, roomFloor: Int): Future[Room] = {

    roomRepo.findByHotel(hotelId).flatMap { roomsInHotel =>
      if (roomsInHotel.exists(_.roomNumber == roomNumber)) {
        Future.failed(new IllegalArgumentException(s"Room $roomNumber already exists in hotel $hotelId"))
      }
      if (roomFloor > 10 && roomFloor < 0) { // 0 is ground floor
        Future.failed(new IllegalArgumentException(s"Floor $roomFloor doesn't exists in hotel $hotelId"))
      } else {
        // Step 2: Create new room

        // get room type Id first from the roomType table
        roomTypeRepo.findByName(roomType).flatMap {
          case Some(roomTypeId) =>
            val now = new Timestamp(System.currentTimeMillis())
            val room = new Room(
              roomId = IDGenerator.generate(IdType.Room),
              hotelId = hotelId,
              roomNumber = roomNumber,
              roomFloor = roomFloor,
              roomTypeId = roomTypeId,
              isAlreadyBooked = false,
              guestId = None,
              createdAt = now, updatedAt = now
            )
            roomRepo.create(room)
          case None =>
            Future.failed(new Exception(s"Room type '$roomType' does not exist"))

        }
      }
    }
  }

  def listRoomsByHotel(hotelId: String): Future[Seq[Room]] =
    roomRepo.findByHotel(hotelId)

  def markAsBooked(hotelId: String, roomNumber: String, guestId: String): Future[Room] = {
    val now = new Timestamp(System.currentTimeMillis())

    roomRepo.findByHotelAndRoomNumber(hotelId, roomNumber).flatMap {
      case None =>
        Future.failed(new NoSuchElementException(
          s"Room $roomNumber not found in hotel $hotelId"
        ))

      case Some(room) if room.isAlreadyBooked =>
        Future.failed(new IllegalStateException(
          s"Room $roomNumber in hotel $hotelId is already booked"
        ))

      case Some(room) =>
        val updatedRoom = room.copy(
          isAlreadyBooked = true,
          guestId = Some(guestId),
          updatedAt = now
        )

        roomRepo.update(updatedRoom).map(_ => updatedRoom)
    }
  }
}
