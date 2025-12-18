package models

import play.api.libs.json.{Format, JsResult, JsString, JsValue, Json, OFormat}

import java.sql.Timestamp

case class SensorReadingByZone (
                                 zoneId: String,
                                 eventTime: Timestamp,
                                 sensorId: String,
                                 pm2_5: Double,
                                 pm10: Double,
                                 co2_ppm: Double,
                                 deviceStatus: String,
                                 processingTimestamp: Timestamp

)
object SensorReadingByZone {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsString(ts.toInstant.toString)
    def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(str => Timestamp.from(java.time.Instant.parse(str)))
  }

  implicit val format: OFormat[SensorReadingByZone] = Json.format[SensorReadingByZone]
}