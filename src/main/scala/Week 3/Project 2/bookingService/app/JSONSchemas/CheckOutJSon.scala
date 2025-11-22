package JSONSchemas

import models.CheckOut
import play.api.libs.json.{Json, Writes}

object CheckOutJSon {
  implicit val checkInWrites: Writes[CheckOut] = Json.writes[CheckOut]
}
