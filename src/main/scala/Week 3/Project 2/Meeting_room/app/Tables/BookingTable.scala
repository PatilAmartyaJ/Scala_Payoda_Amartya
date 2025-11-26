package Tables

import Models.Booking
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.util.UUID

class BookingTable(tag: Tag) extends Table[Booking](tag, "bookings") {

  def booking_id = column[String]("booking_id", O.PrimaryKey)
  def roomId = column[String]("room_id")
  def bookedBy = column[String]("booked_by")
  def employeeId = column[String]("employee_id")
  def title = column[String]("title")
  def description = column[Option[String]]("description")
  def purpose = column[String]("purpose")
  def department = column[String]("department")
  def startTime = column[Timestamp]("start_time")
  def endTime = column[Timestamp]("end_time")
  def expectedDuration = column[Int]("expected_duration")
  def status = column[String]("status")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def room = foreignKey("room_booking_fk", roomId, TableQuery[RoomTable])(_.room_id)
 // def adminStaff = foreignKey("admin_staff_fk", bookedBy, TableQuery[UserTable])(_.user_id)
//  def employee = foreignKey("employee_fk", employeeId, TableQuery[UserTable])(_.user_id)



  override def * =
    (
      booking_id, roomId, bookedBy, employeeId, title, description, purpose, department,
      startTime, endTime, expectedDuration, status, createdAt, updatedAt

    )<> ((Booking.apply _).tupled, Booking.unapply)

}
