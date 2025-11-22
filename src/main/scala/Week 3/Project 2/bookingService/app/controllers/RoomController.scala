package controllers



import Requests.CreateRoomRequest

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import services.RoomService
import models.Room

// Request JSON for creating a room

@Singleton
class RoomController @Inject()(
                                cc: ControllerComponents,
                                roomService: RoomService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JSONSchemas.RoomJson._

  /**
   * Create a room under a specific hotel
   * POST /hotels/:hotelId/rooms
   * Request JSON: { "roomNumber": "101", "roomType": "Deluxe" }
   */
  def createRoom(hotelId: String): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateRoomRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      roomData => {
        roomService.createRoomInHotel(hotelId, roomData.roomNumber, roomData.roomType, roomData.roomFloor).map { room =>
          Created(Json.toJson(room))
        }.recover {
          case e: IllegalArgumentException => Conflict(Json.obj("status" -> "error", "message" -> e.getMessage))
          case e =>
            e.printStackTrace()
            InternalServerError(Json.obj("status" -> "error", "message" -> "Internal server error"))
        }
      }
    )
  }

  /**
   * List all rooms in a hotel
   * GET /hotels/:hotelId/rooms
   */
  def listRooms(hotelId: String): Action[AnyContent] = Action.async {
    roomService.listRoomsByHotel(hotelId).map { rooms =>
      Ok(Json.obj("status" -> "success", "rooms" -> Json.toJson(rooms)))
    }.recover {
      case e =>
        e.printStackTrace()
        InternalServerError(Json.obj("status" -> "error", "message" -> "Unable to fetch rooms"))
    }
  }

  def checkIn(hotelId: String, roomNumber: String, guestId: String) = Action.async {
    roomService.markAsBooked(hotelId, roomNumber, guestId).map { updatedRoom =>
      Ok(Json.toJson(updatedRoom))
    }.recover {
      case e: NoSuchElementException => NotFound(e.getMessage)
      case e: IllegalStateException  => Conflict(e.getMessage)
      case e =>
        e.printStackTrace()
        InternalServerError("Internal server error")
    }
  }

}

// Response JSON (optional, can use Room directly if you have Writes)


