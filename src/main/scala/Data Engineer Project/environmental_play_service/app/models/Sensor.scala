package models

import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp

case class Sensor(
                   sensor_id: String,
                   zone_id: String,
                   sensor_type: String,
                   installed_at: Timestamp,
                   status: String
                 )

object Sensor {
  def create(zone_id: String,
             sensor_type: String,
             status: String
            ): Sensor ={
    val now=new Timestamp(System.currentTimeMillis())
    Sensor(
      sensor_id= IDGenerator.generate(IdType.Sensor),
      zone_id = zone_id,
      sensor_type = sensor_type,
      installed_at=now,
      status=status
    )
  }
}