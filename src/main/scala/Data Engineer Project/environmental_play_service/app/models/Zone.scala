package models

import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp

case class Zone(
                 zone_id: String,
                 name: String,
                 city: String,
                 latitude: Double,
                 longitude: Double,
                 created_at: Timestamp  // or java.sql.Timestamp
               )


object Zone {
  def create(name: String,
             city: String,
             latitude: Double,
             longitude: Double,
              ): Zone ={
    val now=new Timestamp(System.currentTimeMillis())
    Zone(
      zone_id = IDGenerator.generate(IdType.Zone),
      name = name,
      city=city,
      latitude=latitude,
      longitude=longitude,
      created_at = now
    )
  }
}
