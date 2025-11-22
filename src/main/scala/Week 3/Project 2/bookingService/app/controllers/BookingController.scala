package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import services.{AddressService, BookingService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BookingController @Inject()(
                                   cc: ControllerComponents,
                                   bookingService: BookingService
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JSONSchemas.BookingJson._

}
