package controllers

import Requests.{CreateGuestRequest}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{AddressService, GuestService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuestController @Inject()(
                                 cc: ControllerComponents,
                                 guestService: GuestService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JSONSchemas.GuestJson._
  def createGuest(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateGuestRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      guestData => {
        guestService.createGuest(guestData.first_name, guestData.last_name, guestData.email, guestData.phone,guestData.gender,guestData.Id_type,guestData.Id_issue_country,guestData.address_Id,guestData.nationalities).map { guest =>
          Created(Json.toJson(guest))
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


