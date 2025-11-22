package JSONSchemas



import models.Booking
import play.api.libs.json.{Json, Writes}

object BookingJson {
  implicit val bookingWrites: Writes[Booking] = Json.writes[Booking]
}
