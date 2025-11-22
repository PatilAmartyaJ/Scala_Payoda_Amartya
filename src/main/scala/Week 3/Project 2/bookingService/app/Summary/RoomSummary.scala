package Summary

case class RoomSummary(room_Id: String,
                       hotel_Id: String,
                       room_Number: String,
                       room_Floor: Int,
                       room_TypeId: String,
                       is_AlreadyBooked: Boolean,
                       guest_Id: Option[String])
