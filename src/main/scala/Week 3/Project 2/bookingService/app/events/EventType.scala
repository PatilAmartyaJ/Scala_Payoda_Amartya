package events


sealed trait EventType {
  def value: String
}

object EventType {

  case object GuestCheckedIn extends EventType {
    val value = "GUEST_CHECKED_IN"
  }

  case object GuestCheckedOut extends EventType {
    val value = "GUEST_CHECKED_OUT"
  }

  case object RoomBooked extends EventType {
    val value = "ROOM_BOOKED"
  }
}


