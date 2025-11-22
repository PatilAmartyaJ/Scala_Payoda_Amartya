package controllers


import Requests.CreateHotelRequest
import jakarta.inject.Singleton
import models.{Address, Hotel}

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import services.HotelService

import scala.concurrent.{ExecutionContext, Future}



@Singleton
class HotelController @Inject()(
                                 cc: ControllerComponents,
                                 hotelService: HotelService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JSONSchemas.HotelJson._

  def createHotel(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateHotelRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      hotelData => {
        hotelService.createHotel(hotelData.hotelName, hotelData.hotelAddressId, assignedOwnerId = hotelData.assignedOwnerId, totalFloors = hotelData.totalFloors, totalRooms = hotelData.totalRooms).map { hotel =>
          Created(Json.toJson(hotel))
        }.recover {
          case e: IllegalArgumentException => Conflict(Json.obj("status" -> "error", "message" -> e.getMessage))
          case e =>
            e.printStackTrace()
            InternalServerError(Json.obj("status" -> "error", "message" -> "Internal server error"))
        }
      }
    )
  }
}

// Response JSON (optional, can use Room directly if you have Writes)



