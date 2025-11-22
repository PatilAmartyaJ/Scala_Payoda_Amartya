package repositories

import javax.inject.{Inject, Singleton}
import models.Room
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Rooms

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RoomRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val rooms = TableQuery[Rooms]

  def create(room: Room): Future[Room] = {
    val insert = rooms += room
    db.run(insert).map(_ => room)  // Return the same room, since ID is already generated
  }

  def findByHotel(hotelId: String): Future[Seq[Room]] =
    db.run(rooms.filter(_.hotelId === hotelId).result)

  def findById(roomId: String): Future[Option[Room]] =
    db.run(rooms.filter(_.roomId === roomId).result.headOption)

  def roomExists(hotelId: String, roomNumber: String): Future[Boolean] = {
    val query = rooms.filter(r => r.hotelId === hotelId && r.roomNumber === roomNumber).exists
    db.run(query.result)
  }


  def findByHotelAndRoomNumber(hotelId: String, roomNumber: String): Future[Option[Room]] = {
    db.run(
      rooms
        .filter(r => r.hotelId === hotelId && r.roomNumber === roomNumber)
        .result
        .headOption
    )
  }

  def update(room: Room): Future[Int] = {
    db.run(
      rooms.filter(_.roomId === room.roomId)
        .update(room)
    )
  }


}
