package controllers

import Models._
import Requests.CreateRoomRequest
import Services.{RoomAvailabilityResponse, RoomService, RoomUpdate}

import javax.inject._
import play.api.libs.json._
import play.api.mvc._

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomController @Inject()(
                                cc: ControllerComponents,
                                roomService: RoomService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)

  // JSON formats
  import Util.JsonFormats._
  implicit val roomFormat: Format[Room] = Json.format[Room]
  implicit val createRoomRequestFormat: Format[CreateRoomRequest] = Json.format[CreateRoomRequest]
  implicit val roomUpdateFormat: Format[RoomUpdate] = Json.format[RoomUpdate]
  implicit val bookingFormat: Format[Booking] = Json.format[Booking]

  implicit val maintenanceScheduleFormat: Format[MaintenanceSchedule] = Json.format[MaintenanceSchedule]
  implicit val roomAvailabilityResponseFormat: Format[RoomAvailabilityResponse] = Json.format[RoomAvailabilityResponse]

  // RoomFacility JSON formats
  implicit val roomFacilityReads: Reads[RoomFacility] = Reads {
    case JsString("PROJECTOR") => JsSuccess(RoomFacility.Projector)
    case JsString("WHITEBOARD") => JsSuccess(RoomFacility.Whiteboard)
    case JsString("VIDEOCONFERENCING") => JsSuccess(RoomFacility.VideoConferencing)
    case JsString("AUDIOSYSTEM") => JsSuccess(RoomFacility.AudioSystem)
    case _ => JsError("Invalid room facility")
  }

  implicit val roomFacilityWrites: Writes[RoomFacility] = Writes {
    case RoomFacility.Projector => JsString("PROJECTOR")
    case RoomFacility.Whiteboard => JsString("WHITEBOARD")
    case RoomFacility.VideoConferencing => JsString("VIDEOCONFERENCING")
    case RoomFacility.AudioSystem => JsString("AUDIOSYSTEM")
  }

  // RoomAvailability JSON formats
  implicit val roomAvailabilityReads: Reads[RoomAvailability] = Reads {
    case JsString("AVAILABLE") => JsSuccess(RoomAvailability.Available)
    case JsString("UNDERMAINTENANCE") => JsSuccess(RoomAvailability.UnderMaintenance)
    case JsString("RESERVED") => JsSuccess(RoomAvailability.Reserved)
    case _ => JsError("Invalid room availability")
  }

  implicit val roomAvailabilityWrites: Writes[RoomAvailability] = Writes {
    case RoomAvailability.Available => JsString("AVAILABLE")
    case RoomAvailability.UnderMaintenance => JsString("UNDERMAINTENANCE")
    case RoomAvailability.Reserved => JsString("RESERVED")
  }

  def createRoom = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateRoomRequest] match {
      case JsSuccess(roomRequest, _) =>
        roomService.createRoom(roomRequest).map {
          case Right(createdRoom) =>
            logger.info(s"Room created successfully: ${createdRoom.name} (ID: ${createdRoom.room_id})")
            Created(Json.toJson(createdRoom))
          case Left(error) =>
            logger.warn(s"Failed to create room: $error")
            Conflict(Json.obj("error" -> error))
        }.recover {
          case ex: Exception =>
            logger.error("Error creating room", ex)
            InternalServerError(Json.obj("error" -> "Failed to create room"))
        }

      case JsError(errors) =>
        logger.error(s"Invalid create room request: $errors")
        Future.successful(BadRequest(Json.obj("error" -> "Invalid request body", "details" -> JsError.toJson(errors))))
    }
  }
  // Add this case class for create room request

  // Your existing methods (updated to remove authAction)
  def getAllRooms = Action.async { implicit request =>
    roomService.getAllRooms().map { rooms =>
      Ok(Json.toJson(rooms))
    }.recover {
      case ex: Exception =>
        logger.error("Error fetching all rooms", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch rooms"))
    }
  }

  def getRoom(roomId: String) = Action.async { implicit request =>
    roomService.getRoomById(roomId).map {
      case Some(room) => Ok(Json.toJson(room))
      case None => NotFound(Json.obj("error" -> "Room not found"))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error fetching room: $roomId", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch room"))
    }
  }

  def getAvailableRooms(startTime: String, endTime: String) = Action.async { implicit request =>
    roomService.getAvailableRooms(startTime, endTime).map { rooms =>
      Ok(Json.toJson(rooms))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error fetching available rooms from $startTime to $endTime", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch available rooms"))
    }
  }

  def updateRoom(roomId: String) = Action.async(parse.json) { implicit request =>
    request.body.validate[RoomUpdate] match {
      case JsSuccess(roomUpdate, _) =>
        roomService.updateRoom(roomId, roomUpdate).map {
          case true => Ok(Json.obj("message" -> "Room updated successfully"))
          case false => NotFound(Json.obj("error" -> "Room not found"))
        }.recover {
          case ex: Exception =>
            logger.error(s"Error updating room: $roomId", ex)
            InternalServerError(Json.obj("error" -> "Failed to update room"))
        }
      case JsError(errors) =>
        logger.error(s"Invalid update room request: $errors")
        Future.successful(BadRequest(Json.obj("error" -> "Invalid request body", "details" -> JsError.toJson(errors))))
    }
  }

  def deactivateRoom(roomId: String) = Action.async { implicit request =>
    roomService.deactivateRoom(roomId).map {
      case true => Ok(Json.obj("message" -> "Room deactivated successfully"))
      case false => NotFound(Json.obj("error" -> "Room not found"))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error deactivating room: $roomId", ex)
        InternalServerError(Json.obj("error" -> "Failed to deactivate room"))
    }
  }

  def getRoomBookings(roomId: String, date: Option[String]) = Action.async { implicit request =>
    roomService.getRoomBookings(roomId, date).map { bookings =>
      Ok(Json.toJson(bookings))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error fetching bookings for room: $roomId", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch room bookings"))
    }
  }

  def getRoomsByCapacity(minCapacity: Int) = Action.async { implicit request =>
    roomService.getRoomsByCapacity(minCapacity).map { rooms =>
      Ok(Json.toJson(rooms))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error fetching rooms by capacity: $minCapacity", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch rooms by capacity"))
    }
  }

  def getRoomAvailability(roomId: String) = Action.async { implicit request =>
    roomService.getRoomAvailabilityStatus(roomId).map {
      case Some(status) => Ok(Json.toJson(status))
      case None => NotFound(Json.obj("error" -> "Room not found"))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error fetching room availability: $roomId", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch room availability"))
    }
  }

//  def getRoomDetailedStatus(roomId: String) = Action.async { implicit request =>
//    roomService.getRoomDetailedStatus(roomId).map {
//      case Some(status) => Ok(Json.toJson(status))
//      case None => NotFound(Json.obj("error" -> "Room not found"))
//    }.recover {
//      case ex: Exception =>
//        logger.error(s"Error fetching detailed status for room: $roomId", ex)
//        InternalServerError(Json.obj("error" -> "Failed to fetch room status"))
//    }
//  }
//
//  def getAllRoomsWithStatus = Action.async { implicit request =>
//    roomService.getAllRoomsWithStatus().map { rooms =>
//      Ok(Json.toJson(rooms))
//    }.recover {
//      case ex: Exception =>
//        logger.error("Error fetching rooms with status", ex)
//        InternalServerError(Json.obj("error" -> "Failed to fetch rooms with status"))
//    }
//  }

//  def checkAvailabilityWithMaintenance(roomId: String, startTime: String, endTime: String) = Action.async { implicit request =>
//    roomService.checkRoomAvailabilityWithMaintenance(roomId, startTime, endTime).map { available =>
//      Ok(Json.obj("available" -> available))
//    }.recover {
//      case ex: Exception =>
//        logger.error(s"Error checking availability with maintenance for room: $roomId", ex)
//        InternalServerError(Json.obj("error" -> "Failed to check availability"))
//    }
//  }
//
//  def setMaintenance(roomId: String) = Action.async(parse.json) { implicit request =>
//    request.body.validate[MaintenanceSchedule] match {
//      case JsSuccess(maintenance, _) =>
//        roomService.setRoomMaintenance(roomId, maintenance).map {
//          case true => Ok(Json.obj("message" -> "Room set to maintenance mode"))
//          case false => NotFound(Json.obj("error" -> "Room not found"))
//        }.recover {
//          case ex: Exception =>
//            logger.error(s"Error setting maintenance for room: $roomId", ex)
//            InternalServerError(Json.obj("error" -> "Failed to set maintenance"))
//        }
//      case JsError(errors) =>
//        logger.error(s"Invalid maintenance request: $errors")
//        Future.successful(BadRequest(Json.obj("error" -> "Invalid request body", "details" -> JsError.toJson(errors))))
//    }
//  }
//
//  def removeMaintenance(roomId: String) = Action.async { implicit request =>
//    roomService.removeRoomFromMaintenance(roomId).map {
//      case true => Ok(Json.obj("message" -> "Room removed from maintenance"))
//      case false => NotFound(Json.obj("error" -> "Room not found or not in maintenance"))
//    }.recover {
//      case ex: Exception =>
//        logger.error(s"Error removing maintenance for room: $roomId", ex)
//        InternalServerError(Json.obj("error" -> "Failed to remove maintenance"))
//    }
//  }
//
//  def searchRooms = Action.async(parse.json) { implicit request =>
//    request.body.validate[RoomSearchCriteria] match {
//      case JsSuccess(criteria, _) =>
//        roomService.searchRooms(criteria).map { rooms =>
//          Ok(Json.toJson(rooms))
//        }.recover {
//          case ex: Exception =>
//            logger.error("Error searching rooms", ex)
//            InternalServerError(Json.obj("error" -> "Failed to search rooms"))
//        }
//      case JsError(errors) =>
//        logger.error(s"Invalid search criteria: $errors")
//        Future.successful(BadRequest(Json.obj("error" -> "Invalid search criteria", "details" -> JsError.toJson(errors))))
//    }
//  }

  def getCurrentlyAvailableRooms = Action.async { implicit request =>
    roomService.getCurrentlyAvailableRooms().map { rooms =>
      Ok(Json.toJson(rooms))
    }.recover {
      case ex: Exception =>
        logger.error("Error fetching currently available rooms", ex)
        InternalServerError(Json.obj("error" -> "Failed to fetch available rooms"))
    }
  }
}