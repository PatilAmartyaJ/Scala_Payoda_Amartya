package controllers


import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import repositories.HotelRepository
import models.{Hotel, Address}

@Singleton
class TestController @Inject()(
                                cc: ControllerComponents,
                                hotelRepo: HotelRepository
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def testDb = Action.async {
    hotelRepo.testConnection().map { isConnected =>
      if (isConnected) {
        Ok(Json.obj("status" -> "success", "message" -> "Database connected successfully"))
      } else {
        InternalServerError(Json.obj("status" -> "error", "message" -> "Database connection failed"))
      }
    }
  }

  def createTestHotel = Action.async {
    val address = Address.create(
      addressLine1 = "123 Test Street",
      addressLine2 =Some(" abc Nagar"),
      city = "Test City",
      state= Some("Test State"),
      country = "Test Country",
      postalCode=Some("415409")
    )

    val hotel = Hotel.create(
      hotelName = "Test Hotel",
      hotelAddressId = "ADR-abcdefgh",
      totalFloors = 5,
      totalRooms = 50,
      assignedOwnerid= None
    )

    hotelRepo.create(hotel).map { createdHotel =>
      Ok(Json.obj("status" -> "success", "hotelId" -> createdHotel.hotelId))
    }.recover {
      case ex =>
        InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }
//  implicit val addressFormat: Format[Address] = Json.format[Address]
//  implicit val hotelFormat: Format[Hotel] = Json.format[Hotel]
//
//  def listHotels = Action.async {
//    hotelRepo.listAll().map { hotels =>
//      Ok(Json.toJson(hotels))
//    }
//  }
}