package models

import play.api.libs.json.{Json, Writes}

case class DailySummary(
                                  zone_id: String,
                                  event_Date: java.sql.Date,  // or LocalDate if using Java 8+ time API
                                  avg_pm25: Double,
                                  avg_pm10: Double,
                                  avg_co2: Double,

                                )

object DailySummary {
  implicit val writes: Writes[DailySummary] = Json.writes[DailySummary]
//  def fromProtobuf(proto: DailySummary): DailySummary = {
//    DailySummary(
//      zone_id = proto.getZoneId,
//      timestamp = proto.getTimestamp,
//      pm25 = proto.getPm25,
//      pm10 = proto.getPm10,
//      co2 = proto.getCo2,
//      temperature = proto.getTemperature,
//      humidity = proto.getHumidity,
//      anomalies = proto.getAnomaliesList.asScala.map(Anomaly.fromProtobuf).toSeq
//    )
//  }

}