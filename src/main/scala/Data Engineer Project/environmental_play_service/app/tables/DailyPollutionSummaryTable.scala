package tables

import models.DailyPollutionSummary
import slick.jdbc.MySQLProfile.api._

import java.sql.Date
class DailyPollutionSummaryTable(tag: Tag)
  extends Table[DailyPollutionSummary](tag, "daily_pollution_summary") {
  // Or use your actual table name if different from "air_quality_data"

  def zoneId = column[String]("zone_id",O.PrimaryKey)
  def name = column[String]("name")
  def city = column[String]("city")
  def latitude = column[Double]("latitude")
  def longitude = column[Double]("longitude")
  def eventDate = column[Date]("event_date")
  def avgPm25 = column[Double]("avg_pm25")
  def avgPm10 = column[Double]("avg_pm10")
  def avgCo2 = column[Double]("avg_co2")
  def anomalyCount = column[Long]("anomaly_count")

  // Define primary key if needed (adjust based on your actual PK)


  // Foreign key to zones table (if zones table exists)
  // def zoneFk = foreignKey("fk_air_quality_zone", zoneId, TableQuery[Zones])(_.zoneId)

  def * = (
    zoneId,
    name,
    city,
    latitude,
    longitude,
    eventDate,
    avgPm25,
    avgPm10,
    avgCo2,
    anomalyCount
  ).<>((DailyPollutionSummary.apply _).tupled, DailyPollutionSummary.unapply)
}
