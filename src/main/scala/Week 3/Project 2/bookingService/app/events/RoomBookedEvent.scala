package events

import Summary.{BookingSummary, GuestSummary, HotelSummary, RoomSummary}
import util.IDGenerator
import util.IDGenerator.IdType.Event

import java.sql.Timestamp



case class RoomBookedEvent(
                            eventId: String,
                            eventType: EventType=EventType.RoomBooked,
                            timestamp: Timestamp,
                            guestSummary: GuestSummary,
                            roomSummary: RoomSummary,
                            hotelSummary: HotelSummary,
                            bookingSummary:BookingSummary
                          )
object RoomBookedEvent {
  def apply(
             hotelSummary: HotelSummary,
             roomSummary: RoomSummary,
             guestSummary: GuestSummary,
             bookingSummary: BookingSummary
           ): RoomBookedEvent = {
    val timestamp=new Timestamp(System.currentTimeMillis())
    new RoomBookedEvent(
      eventId = IDGenerator.generate(Event),
      eventType = EventType.RoomBooked,
      timestamp = timestamp,
      hotelSummary = hotelSummary,
      roomSummary = roomSummary,
      guestSummary = guestSummary,
      bookingSummary = bookingSummary
    )
  }

}