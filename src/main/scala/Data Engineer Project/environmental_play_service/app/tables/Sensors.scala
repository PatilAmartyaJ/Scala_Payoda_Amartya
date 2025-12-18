package tables
import models.{Sensor, Zone}
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
class Sensors(tag: Tag) extends Table[Sensor](tag, "sensor") {
  def sensorId    = column[String]("sensor_id", O.PrimaryKey)
  def zoneId      = column[String]("zone_id")
  def sensorType  = column[String]("sensor_type")
  def installedAt = column[Timestamp]("installed_at")
  def status      = column[String]("status")

  // FK → zone.zone_id
  def zoneFk = foreignKey(
    "fk_sensor_zone",
    zoneId,
    TableQuery[Zones]
  )(_.zoneId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  def * = (
    sensorId, zoneId, sensorType, installedAt, status
  ).<>((Sensor.apply _).tupled, Sensor.unapply)
}