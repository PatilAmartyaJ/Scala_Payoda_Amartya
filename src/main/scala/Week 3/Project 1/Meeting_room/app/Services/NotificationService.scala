package Services


import Models.{Booking, Notification, NotificationStatus, NotificationType, UserRole}
import Repositories.{BookingRepository, NotificationRepository, RoomRepository, UserRepository}

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
import java.util.UUID

// app/services/NotificationService.scala


@Singleton
class NotificationService @Inject()(
                                     notificationRepo: NotificationRepository,
                                     userRepo: UserRepository,
                                     bookingRepo: BookingRepository,
                                     roomRepo: RoomRepository,
                                     kafkaService: KafkaService
                                   )(implicit ec: ExecutionContext) {

  def sendBookingConfirmation(booking: Booking): Future[Notification] = {
    for {
      employee <- userRepo.findById(booking.employeeId)
      roomOpt <- roomRepo.findById(booking.roomId)
      roomName = roomOpt.map(_.name).getOrElse("N/A")
      subject = s"Meeting Room Booking Confirmation: ${booking.title}"
      message = s"""
        Dear ${employee.map(_.firstName).getOrElse("Employee")},

        Your meeting room booking has been confirmed:
        Room: $roomName
        Time: ${booking.startTime} to ${booking.endTime}
        Purpose: ${booking.purpose}

        Thank you,
        Office Management System
      """
      notification = Notification.create(
      notification_type= NotificationType.BookingConfirmation.toString,
        recipientId = booking.employeeId,
        recipientEmail = employee.map(_.email).getOrElse(""),
        subject = subject,
        message = message,
        bookingId = booking.booking_id,
        scheduledFor = new Timestamp(System.currentTimeMillis()),
        status = NotificationStatus.Sent.toString
      )
      savedNotification <- notificationRepo.create(notification)
      _ <- kafkaService.sendNotification(savedNotification)
    } yield savedNotification
  }

  def notifyRoomServiceTeam(booking: Booking): Future[Seq[Notification]] = {
    for {
      serviceTeam <- userRepo.findByRole(UserRole.RoomServiceTeam)
      roomOpt <- roomRepo.findById(booking.roomId)
      roomName = roomOpt.map(_.name).getOrElse("N/A")
      subject = s"Room Preparation Required: $roomName"
      message = s"""
        Room preparation required for:
        Meeting: ${booking.title}
        Room: $roomName
        Time: ${booking.startTime}
        Duration: ${booking.expectedDuration} minutes
        Special Requirements: ${booking.description.getOrElse("None")}
      """
      notifications <- Future.sequence(serviceTeam.map { teamMember =>
        val notification = Notification.create(
          notification_type = NotificationType.RoomPreparation.toString,
          recipientId = teamMember.user_id,
          recipientEmail = teamMember.email,
          subject = subject,
          message = message,
          bookingId = booking.booking_id,
          scheduledFor = new Timestamp(System.currentTimeMillis()),
          status = NotificationStatus.Sent.toString
        )
        notificationRepo.create(notification).flatMap { saved =>
          kafkaService.sendNotification(saved).map(_ => saved)
        }
      })
    } yield notifications
  }

  def sendReminder(booking: Booking): Future[Notification] = {
    for {
      employee <- userRepo.findById(booking.employeeId)
      roomOpt <- roomRepo.findById(booking.roomId)
      roomName = roomOpt.map(_.name).getOrElse("N/A")
      subject = s"Meeting Reminder: ${booking.title}"
      message = s"""
        Reminder: Your meeting starts in 15 minutes
        Room: $roomName
        Time: ${booking.startTime}
      """
      notification = Notification.create(
        notification_type = NotificationType.ReminderAlert.toString,
        recipientId = booking.employeeId,
        recipientEmail = employee.map(_.email).getOrElse(""),
        subject = subject,
        message = message,
        bookingId = booking.booking_id,
        scheduledFor = Timestamp.valueOf(booking.startTime.toLocalDateTime.minusMinutes(15)),
        status = NotificationStatus.Sent.toString,
      )
      savedNotification <- notificationRepo.create(notification)
      _ <- kafkaService.sendNotification(savedNotification)
    } yield savedNotification
  }

  def sendReleaseNotification(booking: Booking): Future[Seq[Notification]] = {
    for {
      adminStaff <- userRepo.findByRole(UserRole.AdminStaff)
      roomOpt <- roomRepo.findById(booking.roomId)
      roomName = roomOpt.map(_.name).getOrElse("N/A")
      subject = s"Room Released: $roomName"
      message = s"""
        Room $roomName has been released due to no-show.
        Original booking: ${booking.title}
        Scheduled time: ${booking.startTime}
      """
      notifications <- Future.sequence(adminStaff.map { admin =>
        val notification = Notification.create(
          notification_type = NotificationType.ReleaseNotification.toString,
          recipientId = admin.user_id,
          recipientEmail = admin.email,
          subject = subject,
          message = message,
          bookingId = booking.booking_id,
          scheduledFor = new Timestamp(System.currentTimeMillis()),
          status = NotificationStatus.Sent.toString
        )
        notificationRepo.create(notification).flatMap { saved =>
          kafkaService.sendNotification(saved).map(_ => saved)
        }
      })
    } yield notifications
  }

  def sendCancellationNotification(booking: Booking, cancelledBy: String): Future[Notification] = {
    for {
      employee <- userRepo.findById(booking.employeeId)
      cancelledByUser <- userRepo.findById(cancelledBy)
      roomOpt <- roomRepo.findById(booking.roomId)
      roomName = roomOpt.map(_.name).getOrElse("N/A")
      subject = s"Booking Cancelled: ${booking.title}"
      message = s"""
        Your meeting room booking has been cancelled:
        Room: $roomName
        Time: ${booking.startTime} to ${booking.endTime}
        Cancelled by: ${cancelledByUser.map(u => s"${u.firstName} ${u.lastName}").getOrElse("System")}

        If this was a mistake, please contact admin staff.
      """
      notification = Notification.create(
        notification_type = NotificationType.BookingCancellation.toString,
        recipientId = booking.employeeId,
        recipientEmail = employee.map(_.email).getOrElse(""),
        subject = subject,
        message = message,
        bookingId = booking.booking_id,
        scheduledFor = new Timestamp(System.currentTimeMillis()),
        status = NotificationStatus.Sent.toString

      )
      savedNotification <- notificationRepo.create(notification)
      _ <- kafkaService.sendNotification(savedNotification)
    } yield savedNotification
  }
}