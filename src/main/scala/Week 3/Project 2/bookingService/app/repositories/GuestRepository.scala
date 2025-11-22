package repositories

import models.{Guest, Room}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.{Guests, Rooms}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class GuestRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val guests = TableQuery[Guests]
  def create(guest: Guest): Future[Guest] = {
    val insert = guests += guest
    db.run(insert).map(_ => guest)  // Return the same room, since ID is already generated
  }

  def findById(guestId: String): Future[Option[Guest]] =
    db.run(guests.filter(_.guestId === guestId).result.headOption)

}
