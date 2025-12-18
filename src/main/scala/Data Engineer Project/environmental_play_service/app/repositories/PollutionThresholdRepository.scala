package repositories

import models.PollutionThreshold
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.PollutionThresholds

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
class PollutionThresholdRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val thresholds = TableQuery[PollutionThresholds]

  def create(threshold: PollutionThreshold): Future[PollutionThreshold] =
    db.run(thresholds += threshold).map(_ => threshold)

  def findAll(): Future[Seq[PollutionThreshold]] =
    db.run(thresholds.result)

  def findById(thresholdId: String): Future[Option[PollutionThreshold]] =
    db.run(thresholds.filter(_.thresholdId === thresholdId).result.headOption)

  def findByZone(zoneId: String): Future[Seq[PollutionThreshold]] =
    db.run(thresholds.filter(_.zoneId === zoneId).result)
}

