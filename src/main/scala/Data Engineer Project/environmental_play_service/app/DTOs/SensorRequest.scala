package DTOs

import play.api.libs.json.{Json, OWrites, Reads}

import java.sql.Timestamp


case class SensorRequest(
                          zone_id: String,
                          sensor_type: String,
                          status: String
                      )

object SensorRequest {
  implicit val reads: Reads[SensorRequest] = Json.reads[SensorRequest]
  implicit val writes: OWrites[SensorRequest] = Json.writes[SensorRequest]
}


