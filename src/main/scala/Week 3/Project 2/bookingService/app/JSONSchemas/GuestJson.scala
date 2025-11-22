package JSONSchemas

import models.Guest
import play.api.libs.json.{Json, Writes}

object GuestJson {
  implicit val checkInWrites: Writes[Guest] = Json.writes[Guest]
}
