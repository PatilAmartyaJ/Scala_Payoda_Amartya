package Requests

import models.Address
import play.api.libs.json.{Json, Reads}

case class CreateHotelRequest(hotelName: String, hotelAddressId: String, assignedOwnerId: Option[String], totalFloors: Int, totalRooms: Int)

object CreateHotelRequest {
  implicit val reads: Reads[CreateHotelRequest] = Json.reads[CreateHotelRequest]
}

