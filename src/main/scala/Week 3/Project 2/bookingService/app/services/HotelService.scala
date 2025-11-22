package services

import jakarta.inject.Inject
import models.{Address, Hotel}
import repositories.HotelRepository
import util.IDGenerator
import util.IDGenerator.IdType

import java.sql.Timestamp
import java.time.Instant
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HotelService @Inject()(hotelRepo: HotelRepository)(implicit ec: ExecutionContext) {

  /**
   * Creates a new hotel.
   * If you want to enforce uniqueness of (hotelName + hotelAddress),
   * add a check in the repository or here before inserting.
 *
   *
   */
  def createHotel(
                   hotelName: String,
                   hotelAddress_Id: String,
                   assignedOwnerId: Option[String],
                   totalFloors: Int,
                   totalRooms: Int
                 ): Future[Hotel] = {


    val hotel = Hotel.create(hotelName, hotelAddress_Id, assignedOwnerId, totalFloors, totalRooms)

    hotelRepo.create(hotel)
  }
}
