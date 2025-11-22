package mapper

import Summary.RoomSummary
import models.Room

object RoomMapper {
  def summary(room: Room): RoomSummary = {
    RoomSummary(
      room_Id = room.roomId,
      hotel_Id = room.hotelId,
      room_Number = room.roomNumber,
      room_Floor = room.roomFloor,
      room_TypeId = room.roomTypeId,
      is_AlreadyBooked = room.isAlreadyBooked,
      guest_Id = room.guestId
    )
  }
}
