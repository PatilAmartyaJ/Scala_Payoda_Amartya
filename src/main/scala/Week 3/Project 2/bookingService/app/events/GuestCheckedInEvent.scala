package events

import Summary.{BookingSummary, GuestSummary, HotelSummary, RoomSummary}
import util.IDGenerator
import util.IDGenerator.IdType.Event

import java.sql.Timestamp
import java.time.Instant

case class GuestCheckedInEvent(
                                eventId:String,
                                eventType: EventType=EventType.GuestCheckedIn,
                                timestamp: Timestamp,
                                guestSummary: GuestSummary,
                                roomSummary: RoomSummary,
                                hotelSummary: HotelSummary,
                                bookingSummary:BookingSummary
                              )
object GuestCheckedInEvent {
  def apply(
             hotelSummary: HotelSummary,
             roomSummary: RoomSummary,
             guestSummary: GuestSummary,
             bookingSummary: BookingSummary
           ): GuestCheckedInEvent = {
    val timestamp=new Timestamp(System.currentTimeMillis())
    new GuestCheckedInEvent(
      eventId = IDGenerator.generate(Event),
      eventType = EventType.GuestCheckedIn,
      timestamp = timestamp,
      hotelSummary = hotelSummary,
      roomSummary = roomSummary,
      guestSummary = guestSummary,
      bookingSummary = bookingSummary
    )
  }
}