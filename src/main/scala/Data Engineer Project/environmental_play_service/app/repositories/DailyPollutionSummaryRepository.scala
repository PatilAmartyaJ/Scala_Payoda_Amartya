package repositories

import models.{DailyPollutionSummary, PollutionThreshold}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.{DailyPollutionSummaryTable, PollutionThresholds}

import java.sql.Date
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DailyPollutionSummaryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val dailyPollutionSummary = TableQuery[DailyPollutionSummaryTable]

  def findAll(): Future[Seq[DailyPollutionSummary]] =
    db.run(dailyPollutionSummary.result)

  def findByZoneAndDate(date: Date, zoneId: String): Future[Option[DailyPollutionSummary]] =
    db.run(
      dailyPollutionSummary
        .filter(_.eventDate === java.sql.Date.valueOf(date.toString()))
        .filter(_.zoneId === zoneId)
        .result
        .headOption
    )

  // Additional useful methods:

  def findByZoneId(zoneId: String): Future[Seq[DailyPollutionSummary]] =
    db.run(
      dailyPollutionSummary
        .filter(_.zoneId === zoneId)
        .sortBy(_.eventDate.desc)
        .result
    )

  def findByCityAndDateRange(city: String, startDate: Date, endDate: Date): Future[Seq[DailyPollutionSummary]] =
    db.run(
      dailyPollutionSummary
        .filter(_.city === city)
        .filter(_.eventDate >= java.sql.Date.valueOf(startDate.toString()))
        .filter(_.eventDate <= java.sql.Date.valueOf(endDate.toString()))
        .sortBy(_.eventDate.asc)
        .result
    )

  def findByDateRange(startDate: Date, endDate: Date): Future[Seq[DailyPollutionSummary]] =
    db.run(
      dailyPollutionSummary
        .filter(_.eventDate >= java.sql.Date.valueOf(startDate.toString()))
        .filter(_.eventDate <= java.sql.Date.valueOf(endDate.toString()))
        .sortBy(d => (d.eventDate.asc, d.zoneId.asc))
        .result
    )



}
