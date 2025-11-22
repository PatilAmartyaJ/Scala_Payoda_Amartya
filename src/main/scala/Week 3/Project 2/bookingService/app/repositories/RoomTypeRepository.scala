package repositories

import scala.concurrent.ExecutionContext
import models.{Room, RoomType}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.{RoomTypes, Rooms}


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomTypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  val roomTypes = TableQuery[RoomTypes]

  def create(roomType: RoomType): Future[RoomType] =
    db.run(roomTypes += roomType).map(_ => roomType)

  // Find all rooms in a hotel

  // Find by roomId
  def findByName(roomType: String): Future[Option[String]] =
    db.run(roomTypes.filter(_.name === roomType).map(_.roomTypeId).result.headOption)


  def findById(roomTypeId: String): Future[Option[RoomType]] = {
    db.run(roomTypes.filter(_.roomTypeId === roomTypeId).result.headOption)
  }


  // Check if a roomNumber exists for a specific hotel

}
