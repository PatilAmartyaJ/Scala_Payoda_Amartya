package repositories

import Services.{CassandraConnector, S3Connector}
import models.SensorReadingByZone
import play.api.db.slick.DatabaseConfigProvider

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._


class SensorReadingByZoneRepository @Inject()(connector: CassandraConnector, s3connector:S3Connector)(implicit ec: ExecutionContext) {

  private val session = connector.getSession

  // Fetch all rows for a given zone_id
  def findByZone(zoneId: String, limit: Int): List[SensorReadingByZone] = {

    val query =
      """SELECT zone_id, event_time, sensor_id, pm2_5, pm10, co2_ppm,
        |        device_status, processing_timestamp
        | FROM environment_grid.sensor_readings_by_zone
        | WHERE zone_id = ?
        | ORDER BY event_time DESC
        | LIMIT ?""".stripMargin

    val stmt = session.prepare(query)

    session.execute(stmt.bind(zoneId, Int.box(limit))).asScala.map { row =>
      SensorReadingByZone(
        zoneId = row.getString("zone_id"),
        eventTime = Timestamp.from(row.getInstant("event_time")),
        sensorId = row.getString("sensor_id"),
        pm2_5 = row.getDouble("pm2_5"),
        pm10 = row.getDouble("pm10"),
        co2_ppm = row.getDouble("co2_ppm"),
        deviceStatus = row.getString("device_status"),
        processingTimestamp = Timestamp.from(row.getInstant("processing_timestamp"))
      )
    }.toList
  }

  def findTodaysDetails(zoneId: String):Unit={
     val session = s3connector.getSession


  }

}