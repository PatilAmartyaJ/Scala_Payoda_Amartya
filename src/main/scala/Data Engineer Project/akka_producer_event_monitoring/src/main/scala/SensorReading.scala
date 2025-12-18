

import Util.IDGenerator
import Util.IDGenerator.IdType
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.sql.Timestamp

case class SensorReading(
                          sensorId: String,
                          zoneId: String,
                          pm25: Double,
                          pm10: Double,
                          co2Ppm: Double,
                          deviceStatus: String,
                          event_time: Long
                        )

object SensorReading {
  def create(
             zoneId: String,
             pm25: Double,
             pm10: Double,
             co2Ppm: Double,
             deviceStatus: String,
            ): SensorReading ={

    SensorReading(
      sensorId= IDGenerator.generate(IdType.Sensor),
      zoneId = zoneId,
      pm25=pm25,
      pm10 =pm10 ,
      co2Ppm=co2Ppm,
      deviceStatus=deviceStatus,
      event_time= System.currentTimeMillis()
    )
  }
}
object SensorReadingJsonProtocol extends DefaultJsonProtocol {
  implicit val sensorReadingFormat: RootJsonFormat[SensorReading] = jsonFormat7(SensorReading.apply)
}
