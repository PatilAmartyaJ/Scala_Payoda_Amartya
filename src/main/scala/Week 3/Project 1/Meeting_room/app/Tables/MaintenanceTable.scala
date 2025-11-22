package Tables

import Models.{MaintenanceSchedule, Room}
import slick.model.ForeignKeyAction
import slick.jdbc.JdbcType
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDateTime

class MaintenanceTable(tag: Tag) extends Table[MaintenanceSchedule](tag, "maintenance_schedules") {
  def maintenance_id = column[String]("id", O.PrimaryKey)

  def roomId = column[String]("room_id")

  def startTime = column[LocalDateTime]("start_time")

  def endTime = column[LocalDateTime]("end_time")

  def reason = column[String]("reason")

  def maintainedBy = column[String]("maintained_by")

  def createdAt = column[LocalDateTime]("created_at")

  def room = foreignKey("room_fk", roomId, TableQuery[RoomTable])(_.room_id, onDelete = ForeignKeyAction.Cascade)

  def * = (
    maintenance_id, roomId, startTime, endTime, reason, maintainedBy, createdAt
  ).<>((MaintenanceSchedule.apply _).tupled, MaintenanceSchedule.unapply)
}

