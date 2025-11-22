package Tables


import Models.Room
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.time.LocalDateTime

class RoomTable(tag: Tag) extends Table[Room](tag, "rooms") {

  implicit val seqStringColumnType = MappedColumnType.base[Seq[String], String](
    seq => Json.toJson(seq).toString(),
    str => Json.parse(str).as[Seq[String]]
  )

  def room_id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def location = column[String]("location")
  def capacity = column[Int]("capacity")
  def facilities = column[Seq[String]]("facilities", O.SqlType("JSON"))
  def isAvailable = column[Boolean]("is_available")
  def availablility= column[String]("availability")
  def maintenanceSchedule_ID = column[Option[String]]("maintenance_schedule_id") // <── added
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  override def * = (
    room_id, name, location, capacity, facilities, isAvailable,availablility,
    maintenanceSchedule_ID,         // <── included
    createdAt, updatedAt
  ).<>((Room.apply _).tupled, Room.unapply)
}
