package Models


import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp
import java.util.UUID
import java.time.LocalDateTime

case class Notification(
                         notification_id: String,
                         notification_type: String,
                         recipientId: String, // Foreign key to User
                         recipientEmail: String,
                         subject: String,
                         message: String,
                         bookingId: String, // Foreign key to Booking
                         status: String,
                         scheduledFor: Timestamp,
                         sentAt: Timestamp,
                         createdAt: Timestamp
                       )

object Notification {
  def create(notification_type: String,
             recipientId: String, // Foreign key to User
             recipientEmail: String,
             subject: String,
             message: String,
             bookingId: String, // Foreign key to Booking
             status: String,
             scheduledFor: Timestamp): Notification = {
  val now=new Timestamp(System.currentTimeMillis())
    Notification(
      notification_id = IDGenerator.generate(IdType.Notification),
      notification_type = notification_type,
      recipientId = recipientId, // Foreign key to User
      recipientEmail = recipientEmail,
      subject = subject,
      message = message,
      bookingId = bookingId, // Foreign key to Booking
      status = status,
      scheduledFor= scheduledFor,
      sentAt = now, createdAt = now
    )
  }
}

sealed trait NotificationType {
  def toString: String
}

object NotificationType {
  case object BookingConfirmation extends NotificationType {
    override def toString: String = "BOOKING_CONFIRMATION"
  }

  case object RoomPreparation extends NotificationType {
    override def toString: String = "ROOM_PREPARATION"
  }

  case object ReminderAlert extends NotificationType {
    override def toString: String = "REMINDER_ALERT"
  }

  case object ReleaseNotification extends NotificationType {
    override def toString: String = "RELEASE_NOTIFICATION"
  }

  case object BookingCancellation extends NotificationType {
    override def toString: String = "BOOKING_CANCELLATION"
  }
}

sealed trait NotificationStatus {
  def toString: String
}

object NotificationStatus {
  case object Pending extends NotificationStatus {
    override def toString: String = "PENDING"
  }

  case object Sent extends NotificationStatus {
    override def toString: String = "SENT"
  }

  case object Failed extends NotificationStatus {
    override def toString: String = "FAILED"
  }
}