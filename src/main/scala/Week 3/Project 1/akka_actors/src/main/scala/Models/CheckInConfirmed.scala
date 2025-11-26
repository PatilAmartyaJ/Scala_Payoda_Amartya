package Models

sealed trait BookingEvent
case class CheckInConfirmed(
                             checkInId: String,
                             guestEmail: String,
                             roomId: String,
                             roomType: String
                           ) extends BookingEvent

case class GuestCheckout(
                          checkInId: String
                        ) extends BookingEvent

