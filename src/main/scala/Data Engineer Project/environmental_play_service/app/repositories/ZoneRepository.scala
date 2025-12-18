package repositories

import models.Zone
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Zones

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
class ZoneRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val zones = TableQuery[Zones]

  def create(zone: Zone): Future[Zone] =
    db.run(zones += zone).map(_ => zone)

  def findAll(): Future[Seq[Zone]] =
    db.run(zones.result)

  def findById(zoneId: String): Future[Option[Zone]] =
    db.run(zones.filter(_.zoneId === zoneId).result.headOption)
}
