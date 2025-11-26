package Tables


import Models.{Booking, Notification, User}
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import java.time.LocalDateTime

class NotificationsTable(tag: Tag) extends Table[Notification](tag, "notifications") {
  def notification_id = column[String]("id", O.PrimaryKey)
  def notificationType = column[String]("notification_type")
  def recipientId = column[String]("recipient_id")
  def recipientEmail = column[String]("recipient_email")
  def subject = column[String]("subject")
  def message = column[String]("message")
  def bookingId = column[String]("booking_id")
  def status = column[String]("status")
  def scheduledFor = column[Timestamp]("scheduled_for")
  def sentAt = column[Timestamp]("sent_at")
  def createdAt = column[Timestamp]("created_at")

  def recipient = foreignKey("recipient_fk", recipientId, TableQuery[UserTable])(_.user_id)
  def booking = foreignKey("booking_fk", bookingId, TableQuery[BookingTable])(_.booking_id)

  def * = (
    notification_id, notificationType, recipientId, recipientEmail, subject, message,
    bookingId, status, scheduledFor, sentAt, createdAt
  ).<>((Notification.apply _).tupled, Notification.unapply)
}
