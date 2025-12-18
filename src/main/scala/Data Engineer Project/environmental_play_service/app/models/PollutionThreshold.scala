package models

import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp

case class PollutionThreshold(
                               threshold_id: String,
                               zone_id: String,
                               pm2_5_limit: Double,
                               pm10_limit: Double,
                               co2_limit: Double,
                               alert_level: String
                             )

object PollutionThreshold {
  def create(zone_id: String,
             pm2_5_limit: Double,
             pm10_limit: Double,
             co2_limit: Double,
             alert_level: String
            ): PollutionThreshold = {

    PollutionThreshold(
      threshold_id = IDGenerator.generate(IdType.PollutionThreshold),
      zone_id=zone_id,
      pm2_5_limit=pm2_5_limit,
      pm10_limit=pm10_limit,
      co2_limit=co2_limit,
      alert_level=alert_level
    )
  }
}