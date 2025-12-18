package Services

import jakarta.inject.Inject
import models.{Sensor, Zone}
import repositories.{SensorRepository, ZoneRepository}

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

class SensorService@Inject()(sensorRepo: SensorRepository)(implicit ec: ExecutionContext) {
  def createSensor(
                    zone_id: String,
                    sensor_type: String,
                    status: String
                ): Future[Sensor] = {


    val sensor = Sensor.create(zone_id,sensor_type,status)

    sensorRepo.create(sensor)
  }

  def getAllSensors(): Future[Seq[Sensor]] = {
    sensorRepo.findAll()
  }

}
