package Repositories

import Models.{Notification, NotificationStatus}
import Tables.NotificationsTable
import java.sql.Timestamp
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile


// app/repositories/NotificationRepository.scala

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

@Singleton
class NotificationRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  val notifications = TableQuery[NotificationsTable]

  def create(notification: Notification): Future[Notification] = {
    db.run((notifications returning notifications) += notification)
  }

  def findPendingNotifications(): Future[Seq[Notification]] = {
    val now = new Timestamp(System.currentTimeMillis())
    db.run(
      notifications.filter(n => n.status === NotificationStatus.Pending.toString && n.scheduledFor <= now).result
    )
  }

  def markAsSent(notificationId: String): Future[Int] = {
    db.run(
      notifications.filter(_.notification_id === notificationId)
        .map(n => (n.status, n.sentAt))
        .update((NotificationStatus.Sent.toString, new Timestamp(System.currentTimeMillis())))
    )
  }

  def markAsFailed(notificationId: String): Future[Int] = {
    db.run(
      notifications.filter(_.notification_id === notificationId)
        .map(_.status)
        .update(NotificationStatus.Failed.toString)
    )
  }

  def findByBookingId(bookingId: String): Future[Seq[Notification]] = {
    db.run(
      notifications.filter(_.bookingId === bookingId).result
    )
  }
}

