package Services

import Models.{Booking, MaintenanceSchedule, Room, RoomAvailability, RoomFacility}
import Repositories.{BookingRepository, RoomRepository, UserRepository}
import Requests.CreateRoomRequest

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime



@Singleton
class RoomService @Inject()(
                             roomRepo: RoomRepository,
                             bookingRepo: BookingRepository,
                             userRepo: UserRepository
                           )(implicit ec: ExecutionContext) {


  def getAllRooms(): Future[Seq[Room]] = {
    roomRepo.findAll()
  }

  def getRoomById(roomId: String): Future[Option[Room]] = {
    roomRepo.findById(roomId)
  }

  def getAvailableRooms(startTime: String, endTime: String): Future[Seq[Room]] = {
    val start = Timestamp.valueOf(startTime)
    val end = Timestamp.valueOf(endTime)

    for {
      allRooms <- roomRepo.findAll()
      availableRooms <- Future.sequence(
        allRooms.map { room =>
          roomRepo.checkAvailability(room.room_id, start, end).map {
            case true => Some(room)
            case false => None
          }
        }
      ).map(_.flatten)
    } yield availableRooms
  }

  def createRoom(roomRequest: CreateRoomRequest): Future[Either[String, Room]] = {
    // Check if room name already exists
    roomRepo.findByName(roomRequest.name).flatMap {
      case Some(_) =>
        Future.successful(Left("Room with this name already exists"))
      case None =>
        // Check if location is already taken by another room
        roomRepo.findByLocation(roomRequest.location).flatMap {
          case Some(_) =>
            Future.successful(Left("Location is already occupied by another room"))
          case None =>
            val room = Room.create(
              name = roomRequest.name,
              location = roomRequest.location,
              capacity = roomRequest.capacity,
              facilities = roomRequest.facilities.map(_.toString),

            )
            roomRepo.create(room).map(Right(_))
        }
    }
  }

  def updateRoom(roomId: String, roomUpdate: RoomUpdate): Future[Boolean] = {
    roomRepo.findById(roomId).flatMap {
      case Some(existingRoom) =>
        val updatedRoom = existingRoom.copy(
          name = roomUpdate.name.getOrElse(existingRoom.name),
          location = roomUpdate.location.getOrElse(existingRoom.location),
          capacity = roomUpdate.capacity.getOrElse(existingRoom.capacity),
          facilities = roomUpdate.facilities.getOrElse(existingRoom.facilities),
          isAvailable = roomUpdate.isAvailable.getOrElse(existingRoom.isAvailable),
          availability = roomUpdate.availability.getOrElse(existingRoom.availability),
          updatedAt = new Timestamp(System.currentTimeMillis())
        )
        roomRepo.update(updatedRoom).map(_ > 0)
      case None => Future.successful(false)
    }
  }

//  def setRoomMaintenance(roomId: String, maintenanceSchedule: MaintenanceSchedule): Future[Boolean] = {
//    for {
//      roomOpt <- roomRepo.findById(roomId)
//      result <- roomOpt match {
//        case Some(room) =>
//          for {
//            _ <- roomRepo.createMaintenanceSchedule(maintenanceSchedule)
//            updatedRoom = room.copy(
//              availability = RoomAvailability.UnderMaintenance.toString,
//              updatedAt = LocalDateTime.now()
//            )
//            updateResult <- roomRepo.update(updatedRoom)
//          } yield updateResult > 0
//        case None => Future.successful(false)
//      }
//    } yield result
//  }

//  def removeRoomFromMaintenance(roomId: String): Future[Boolean] = {
//    for {
//      maintenanceOpt <- roomRepo.findMaintenanceByRoomId(roomId)
//      roomOpt <- roomRepo.findById(roomId)
//      result <- (maintenanceOpt, roomOpt) match {
//        case (Some(maintenance), Some(room)) =>
//          for {
//            _ <- roomRepo.deleteMaintenanceSchedule(maintenance.maintenance_id)
//            updatedRoom = room.copy(
//              availability = RoomAvailability.Available.toString,
//              updatedAt = LocalDateTime.now()
//            )
//            updateResult <- roomRepo.update(updatedRoom)
//          } yield updateResult > 0
//        case _ => Future.successful(false)
//      }
//    } yield result
//  }

  def deactivateRoom(roomId: String): Future[Boolean] = {
    for {
      roomOpt <- roomRepo.findById(roomId)
      result <- roomOpt match {
        case Some(room) =>
          val updatedRoom = room.copy(
            isAvailable = false,
            availability = RoomAvailability.UnderMaintenance.toString,
            updatedAt = new Timestamp(System.currentTimeMillis())
          )
          roomRepo.update(updatedRoom).map(_ > 0)
        case None => Future.successful(false)
      }
    } yield result
  }

  def getRoomBookings(roomId: String, date: Option[String] = None): Future[Seq[Booking]] = {
    val targetDate = date.map(LocalDateTime.parse).getOrElse(LocalDateTime.now())
    val startOfDay_l = targetDate.toLocalDate.atStartOfDay()
    val endOfDay_l= targetDate.toLocalDate.plusDays(1).atStartOfDay()
    val startOfDay: Timestamp = Timestamp.valueOf(startOfDay_l)
    val endOfDay: Timestamp   = Timestamp.valueOf(endOfDay_l)


    bookingRepo.findByRoomAndTime(roomId, startOfDay, endOfDay)
  }

  def getRoomsByCapacity(minCapacity: Int): Future[Seq[Room]] = {
    roomRepo.findByCapacity(minCapacity)
  }



  def getRoomAvailabilityStatus(roomId: String): Future[Option[RoomAvailabilityResponse]] = {
    for {
      roomOpt <- roomRepo.findById(roomId)
      currentBookings <- bookingRepo.findActiveBookingsForRoom(roomId)
      nextBookings <- bookingRepo.findUpcomingBookingsForRoom(roomId, 24)
    } yield roomOpt.map { room =>
      RoomAvailabilityResponse(
        room = room,
        currentStatus = room.availability,
        currentBookings = currentBookings,
        nextBookings = nextBookings.take(5),
        maintenanceSchedule = None
      )
    }
  }

//  def searchRooms(searchCriteria: RoomSearchCriteria): Future[Seq[Room]] = {
//    roomRepo.searchRooms(searchCriteria)
//  }

  def getCurrentlyAvailableRooms(): Future[Seq[Room]] = {
    roomRepo.getCurrentlyAvailableRooms()
  }


}

case class RoomUpdate(
                       name: Option[String] = None,
                       location: Option[String] = None,
                       capacity: Option[Int] = None,
                       facilities: Option[Seq[String]] = None,
                       isAvailable: Option[Boolean] = None,
                       availability: Option[String] = None
                     )

case class RoomAvailabilityResponse(
                                     room: Room,
                                     currentStatus: String,
                                     currentBookings: Seq[Booking],
                                     nextBookings: Seq[Booking],
                                     maintenanceSchedule: Option[MaintenanceSchedule]
                                   )

case class RoomSearchCriteria(
                               minCapacity: Option[Int] = None,
                               facilities: Seq[String] = Seq.empty,
                               location: Option[String] = None,
                               isActive: Option[Boolean] = Some(true)
                             )

