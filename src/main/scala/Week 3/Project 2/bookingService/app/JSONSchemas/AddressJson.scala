package JSONSchemas

import models.Address
import play.api.libs.json.{Json, Writes}

object AddressJson {
  implicit val addressWrites: Writes[Address] = Json.writes[Address]
}
