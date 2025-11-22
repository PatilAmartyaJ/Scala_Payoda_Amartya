package repositories

import models.{Address, Hotel, Room}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile
import tables.{Hotels,Addresses}


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HotelRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

   val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  //implicit val addressFormat: Format[Address] = Json.format[Address]


  val hotels = TableQuery[Hotels]
  val addresses=TableQuery[Addresses]
  def testConnection(): Future[Boolean] = {
    db.run(sql"SELECT 1".as[Int]).map(_.contains(1))
      .recover { case ex =>
        println(s"Database connection failed: ${ex.getMessage}")
        false
      }
  }

  private def sameAddress(a: Addresses, in: Address): Rep[Boolean] = {
     a.addressLine1 === in.addressLine1 &&
      a.city === in.city &&
      a.country === in.country

  }

  //&& h.hotelAddress === hotel.hotelAddress
  // Create a room
  def create(hotel: Hotel): Future[Hotel] = {
    // Check if a hotel with the same name AND address exists

    val checkAddress = addresses.filter(_.addressId === hotel.hotelAddressId).result.headOption

    val query = addresses.filter(_.addressId === hotel.hotelAddressId).result.headOption

    db.run(query).flatMap {
      case Some(_) =>
        // Address exists, insert hotel
        db.run(hotels += hotel).map(_ => hotel)

      case None =>
        // Address does not exist, fail
        Future.failed(new IllegalArgumentException(
          s"Address with ID ${hotel.hotelAddressId} does not exist"
        ))
    }
  }

  def findById(hotelId: String): Future[Option[Hotel]] =
    db.run(hotels.filter(_.hotelId === hotelId).result.headOption)



  // Find all rooms in a hotel
//  def findByHotel(hotelId: String): Future[Seq[Room]] =
//    db.run(rooms.filter(_.hotelId === hotelId).result)
//
//  // Find by roomId
//  def findByName(roomId: String): Future[Option[Room]] =
//    db.run(rooms.filter(_.roomId === roomId).result.headOption)
//
//  // Check if a roomNumber exists for a specific hotel
//  def roomExists(hotelId: String, roomNumber: String): Future[Boolean] = {
//    val query = rooms.filter(r => r.hotelId === hotelId && r.roomNumber === roomNumber).exists
//    db.run(query.result)
//  }result
}
