package repositories

import models.Booking
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Bookings

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookingRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val bookings = TableQuery[Bookings]

  def create(booking: Booking): Future[Booking] = {
    db.run(bookings += booking).map(_ => booking)
  }

  def findById(bookingId: String): Future[Option[Booking]] =
    db.run(bookings.filter(_.bookingId === bookingId).result.headOption)

  def findByGuest(guestId: String): Future[Seq[Booking]] =
    db.run(bookings.filter(_.guestId === guestId).result)

  def findByRoom(roomId: String): Future[Seq[Booking]] =
    db.run(bookings.filter(_.roomId === roomId).result)

}
