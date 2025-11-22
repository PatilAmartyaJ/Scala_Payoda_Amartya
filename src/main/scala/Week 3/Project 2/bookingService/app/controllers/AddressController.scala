package controllers

import Requests.CreateAddressRequest
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{AddressService, HotelService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject()(
                                   cc: ControllerComponents,
                                   addressService: AddressService
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JSONSchemas.AddressJson._
  def createAddress(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateAddressRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      addressData => {
        addressService.createAddress(addressData.addressLine1, addressData.addressLine2, addressData.city, addressData.state, addressData.country,addressData.postalCode).map { hotel =>
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
