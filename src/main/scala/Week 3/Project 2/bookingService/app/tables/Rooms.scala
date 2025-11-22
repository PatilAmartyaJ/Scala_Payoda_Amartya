package tables
import models.Room

import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp

class Rooms(tag: Tag) extends Table[Room](tag, "rooms") {
  def roomId = column[String]("room_id", O.PrimaryKey)
  def hotelId = column[String]("hotel_id")
  def roomNumber = column[String]("room_number")
  def floor = column[Int]("floor")
  def roomTypeId = column[String]("room_type_id")
  def isAlreadyBooked=column[Boolean]("is_already_booked")
  def guestId = column[Option[String]]("guestId")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[java.sql.Timestamp]("updated_at")



  def hotelFk = foreignKey(
    "fk_room_hotel",
    hotelId,
    TableQuery[Hotels]
  )(_.hotelId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  // references room_types(room_type_id)
  def roomTypeFk = foreignKey(
    "fk_room_roomtype",
    roomTypeId,
    TableQuery[RoomTypes]
  )(_.roomTypeId, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Restrict)

  // references guests(guest_id)
  def guestFk = foreignKey(
    "fk_room_guest",
    guestId,
    TableQuery[Guests]
  )(_.guestId.?,   // because guestId is Option[String]
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.SetNull
  )

  // -------------------------
  // Unique Constraint
  // -------------------------
  def uniqueRoomNumber =
    index("idx_unique_room_hotel", (hotelId, roomNumber), unique = true)


  def * = (
    roomId,
    hotelId,
    roomNumber,
    floor,
    roomTypeId,
    isAlreadyBooked,
    guestId,
    createdAt,
    updatedAt
  ).<>((Room.apply _).tupled, Room.unapply)
}

