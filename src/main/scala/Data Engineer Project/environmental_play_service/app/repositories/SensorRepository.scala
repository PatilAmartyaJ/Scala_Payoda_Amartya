package repositories

import models.Sensor
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Sensors

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SensorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val sensors = TableQuery[Sensors]

  def create(sensor: Sensor): Future[Sensor] =
    db.run(sensors += sensor).map(_ => sensor)

  def findAll(): Future[Seq[Sensor]] =
    db.run(sensors.result)

  def findById(sensorId: String): Future[Option[Sensor]] =
    db.run(sensors.filter(_.sensorId === sensorId).result.headOption)

  def findByZone(zoneId: String): Future[Seq[Sensor]] =
    db.run(sensors.filter(_.zoneId === zoneId).result)
}

