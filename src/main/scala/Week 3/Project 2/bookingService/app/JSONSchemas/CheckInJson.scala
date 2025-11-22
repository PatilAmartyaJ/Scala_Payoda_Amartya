package JSONSchemas

import models.CheckIn
import play.api.libs.json.{Json, Writes}

object CheckInJson {
  implicit val checkInWrites: Writes[CheckIn] = Json.writes[CheckIn]
}
