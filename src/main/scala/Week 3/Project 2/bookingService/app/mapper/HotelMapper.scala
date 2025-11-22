package mapper

import Summary.HotelSummary
import models.Hotel

object HotelMapper {
  def toSummary(hotel: Hotel): HotelSummary =
    HotelSummary(
      hotel_Id = hotel.hotelId,
      hotel_Address_Id = hotel.hotelAddressId
    )
}
