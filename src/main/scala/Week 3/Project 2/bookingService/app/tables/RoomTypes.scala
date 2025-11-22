package tables

import slick.jdbc.MySQLProfile.api._
import play.api.libs.json._
import java.sql.Timestamp
import models.RoomType

class RoomTypes(tag: Tag) extends Table[RoomType](tag, "room_types") {

  // JSON mapping for Seq[String]
  implicit val seqStringColumnType = MappedColumnType.base[Seq[String], String](
    seq => Json.toJson(seq).toString(),       // Seq[String] -> JSON String
    str => Json.parse(str).as[Seq[String]]   // JSON String -> Seq[String]
  )

  def roomTypeId   = column[String]("room_type_id", O.PrimaryKey)
  def code         = column[String]("code")
  def name         = column[String]("name")
  def maxOccupancy = column[Int]("max_occupancy")
  def defaultRate  = column[BigDecimal]("default_rate")
  def amenities    = column[Seq[String]]("amenities", O.SqlType("JSON"))
  def createdAt    = column[Timestamp]("created_at")
  def updatedAt    = column[Timestamp]("updated_at")

  def * = (
    roomTypeId,
    code,
    name,
    maxOccupancy,
    defaultRate,
    amenities,
    createdAt,
    updatedAt
  ).<>((RoomType.apply _).tupled, RoomType.unapply)
}
