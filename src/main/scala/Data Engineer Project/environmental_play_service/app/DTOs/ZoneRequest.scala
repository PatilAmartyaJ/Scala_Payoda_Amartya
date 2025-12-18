package DTOs

import play.api.libs.json.{Json, OWrites, Reads}


case class ZoneRequest(
                        name: String,
                        city: String,
                        latitude: Double,
                        longitude: Double,
                      )

object ZoneRequest {
  implicit val reads: Reads[ZoneRequest] = Json.reads[ZoneRequest]
  implicit val writes: OWrites[ZoneRequest] = Json.writes[ZoneRequest]
}
/*
*/
