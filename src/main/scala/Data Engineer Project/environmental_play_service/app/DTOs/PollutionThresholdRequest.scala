package DTOs

import play.api.libs.json.{Json, OWrites, Reads}

case class PollutionThresholdRequest(
                                      zone_id: String,
                                      pm2_5_limit: Double,
                                      pm10_limit: Double,
                                      co2_limit: Double,
                                      alert_level: String
                                    )


object PollutionThresholdRequest{
  implicit val reads: Reads[PollutionThresholdRequest] = Json.reads[PollutionThresholdRequest]
  implicit val writes: OWrites[PollutionThresholdRequest] = Json.writes[PollutionThresholdRequest]
}
