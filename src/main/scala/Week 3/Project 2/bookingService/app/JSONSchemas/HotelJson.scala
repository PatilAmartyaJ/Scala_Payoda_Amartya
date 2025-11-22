package JSONSchemas

import models.Hotel
import play.api.libs.json.{Json, Writes}

object HotelJson {
  implicit val hotelWrites: Writes[Hotel] = Json.writes[Hotel]
}

