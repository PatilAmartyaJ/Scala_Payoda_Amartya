package Models

object JsonFormats {
  case class Guest(
                    email: String,
                    full_name: String,
                    guest_id: String
                  )

  case class RoomType(
                       name: String
                     )

  case class Room(
                   roomNumber: String,
                   id: String,
                   category: RoomType
                 )
  case class BookingEvent(
                         guest:Guest,
                         roomType: RoomType,
                         room: Room
                         )

}
