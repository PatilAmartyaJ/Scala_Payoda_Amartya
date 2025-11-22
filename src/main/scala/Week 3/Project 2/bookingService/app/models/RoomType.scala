package models

import java.sql.Timestamp
import util.IDGenerator
import util.IDGenerator.IdType

case class RoomType(
                     roomTypeId: String,
                     code: String,
                     name: String,
                     maxOccupancy: Int,
                     defaultRate: BigDecimal,
                     amenities: Seq[String],
                     createdAt: Timestamp,
                     updatedAt: Timestamp
                   )

object RoomType {
  def create(code: String, name: String, maxOccupancy: Int, defaultRate: BigDecimal, amenities: Seq[String]): RoomType = {
    val now = new Timestamp(System.currentTimeMillis())
    RoomType(
      roomTypeId = IDGenerator.generate(IdType.RoomType),
      code = code,
      name = name,
      maxOccupancy = maxOccupancy,
      defaultRate = defaultRate,
      amenities = amenities,
      createdAt = now,
      updatedAt = now
    )
  }
}
