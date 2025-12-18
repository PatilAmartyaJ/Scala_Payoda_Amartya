package tables

import models.PollutionThreshold
import slick.jdbc.MySQLProfile.api._
class PollutionThresholds(tag: Tag)
  extends Table[PollutionThreshold](tag, "pollution_threshold") {

  def thresholdId = column[String]("threshold_id", O.PrimaryKey)
  def zoneId      = column[String]("zone_id")
  def pm25Limit   = column[Double]("pm2_5_limit")
  def pm10Limit   = column[Double]("pm10_limit")
  def co2Limit    = column[Double]("co2_limit")
  def alertLevel  = column[String]("alert_level")

  // FK → zone.zone_id
  def zoneFk = foreignKey(
    "fk_threshold_zone",
    zoneId,
    TableQuery[Zones]
  )(_.zoneId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  def * = (
    thresholdId, zoneId, pm25Limit, pm10Limit, co2Limit, alertLevel
  ).<>((PollutionThreshold.apply _).tupled, PollutionThreshold.unapply)
}


