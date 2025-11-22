package JSONSchemas

import models.Room
import play.api.libs.json.{Json, Writes}

object RoomJson {
  implicit val roomWrites: Writes[Room] = Json.writes[Room]
}

