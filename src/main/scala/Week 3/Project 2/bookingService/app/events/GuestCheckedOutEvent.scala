package events

import Summary.{GuestSummary, HotelSummary, RoomSummary}
import util.IDGenerator
import util.IDGenerator.IdType.Event

import java.sql.Timestamp

case class GuestCheckedOutEvent(
                                 eventId:String,
                                 eventType: EventType=EventType.GuestCheckedOut,
                                 timestamp: Timestamp,
                                 guestSummary: GuestSummary,
                                 roomSummary: RoomSummary,
                                 hotelSummary: HotelSummary
                               )

object GuestCheckedOutEvent {
  def apply(
             hotelSummary: HotelSummary,
             roomSummary: RoomSummary,
             guestSummary: GuestSummary

           ): GuestCheckedOutEvent = {
    val timestamp=new Timestamp(System.currentTimeMillis())
    new GuestCheckedOutEvent(
      eventId = IDGenerator.generate(Event),
      eventType = EventType.GuestCheckedOut,
      timestamp = timestamp,
      hotelSummary = hotelSummary,
      roomSummary = roomSummary,
      guestSummary = guestSummary
    )
  }
}
