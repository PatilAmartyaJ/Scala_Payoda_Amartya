package Repositories

import Models.{Booking, BookingStatus}
import Tables.{BookingTable, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class BookingRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  private val bookings = TableQuery[BookingTable]

  def create(booking: Booking): Future[Booking] = {
    val insert = bookings += booking
    db.run(insert).map(_ => booking)
  }

  def findById(id: String): Future[Option[Booking]] = {
    db.run(bookings.filter(_.booking_id === id).result.headOption)
  }

  def findByRoomAndTime(roomId: String, startTime: Timestamp, endTime: Timestamp): Future[Seq[Booking]] = {
    db.run(
      bookings.filter { booking =>
        booking.roomId === roomId &&
          booking.status =!= "Cancelled" &&
          ((booking.startTime >= startTime && booking.startTime < endTime) ||
            (booking.endTime > startTime && booking.endTime <= endTime) ||
            (booking.startTime <= startTime && booking.endTime >= endTime))
      }.result
    )
  }


  def updateStatus(bookingId: String, status: BookingStatus): Future[Int] = {
    db.run(
      bookings.filter(_.booking_id === bookingId)
        .map(b => (b.status, b.updatedAt))
        .update((status.toString,   new Timestamp(System.currentTimeMillis())))
    )
  }

  def findUpcomingBookings(minutes: Int): Future[Seq[Booking]] = {
    val now = new Timestamp(System.currentTimeMillis())
    val threshold: Timestamp = Timestamp.valueOf(
      now.toLocalDateTime.plusMinutes(minutes)
    )

    db.run(
      bookings.filter { booking =>
        booking.startTime >= now &&
          booking.startTime <= threshold &&
          booking.status === "Confirmed"
      }.result
    )
  }

  def findActiveBookings(): Future[Seq[Booking]] = {
    val now = new Timestamp(System.currentTimeMillis())
    db.run(
      bookings.filter { booking =>
        booking.startTime <= now &&
          booking.endTime >= now &&
          booking.status === "Confirmed"
      }.result
    )
  }

  def findActiveBookingsForRoom(roomId: String): Future[Seq[Booking]] = {
    val now = new Timestamp(System.currentTimeMillis())
    db.run(
      bookings.filter { booking =>
          booking.roomId === roomId &&
            booking.startTime <= now &&
            booking.endTime >= now &&
            (booking.status === BookingStatus.Confirmed.toString ||
              booking.status === BookingStatus.InProgress.toString)
        }
        .sortBy(_.startTime.asc)
        .result
    )
  }

  def findUpcomingBookingsForRoom(roomId: String, hours: Int): Future[Seq[Booking]] = {
    val now = new Timestamp(System.currentTimeMillis())
    val endTime: Timestamp = Timestamp.valueOf(
      now.toLocalDateTime.plusHours(hours)
    )


    db.run(
      bookings.filter { booking =>
          booking.roomId === roomId &&
            booking.startTime >= now &&
            booking.startTime <= endTime &&
            booking.status === BookingStatus.Confirmed.toString
        }
        .sortBy(_.startTime.asc)
        .result
    )
  }

  def findByEmployeeId(employeeId: String): Future[Seq[Booking]] = {
    db.run(
      bookings.filter(_.employeeId === employeeId)
        .sortBy(_.startTime.desc)
        .result
    )
  }

  def findAll(): Future[Seq[Booking]] = {
    db.run(bookings.result)
  }

  def findByBookedBy(adminId: String): Future[Seq[Booking]] = {
    db.run(
      bookings
        .filter(_.bookedBy === adminId)
        .sortBy(_.createdAt.desc)
        .result
    )
  }

  def findConflictingBookings(roomId: String, startTime: Timestamp, endTime: Timestamp): Future[Seq[Booking]] = {
    db.run(
      bookings.filter { booking =>
          booking.roomId === roomId &&
            booking.status =!= BookingStatus.Cancelled.toString &&
            booking.status =!= BookingStatus.Completed.toString &&
            booking.status =!= BookingStatus.Released.toString &&
            ((booking.startTime <= endTime && booking.endTime >= startTime) ||
              (booking.startTime >= startTime && booking.startTime < endTime) ||
              (booking.endTime > startTime && booking.endTime <= endTime))
        }
        .sortBy(_.startTime.asc)
        .result
    )
  }



}
