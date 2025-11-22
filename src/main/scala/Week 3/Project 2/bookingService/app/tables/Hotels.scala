package tables

import models.{Address, Hotel}
import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp

class Hotels (tag: Tag) extends Table[Hotel](tag, "hotels"){



  def hotelId = column[String]("hotel_id", O.PrimaryKey)
  def hotelName = column[String]("hotel_name")
  def hotelAddressId = column[String]("hotel_address_id")
  def assignedOwnerId = column[Option[String]]("assigned_owner_id")
  def totalFloors = column[Int]("total_floors")
  def totalRooms = column[Int]("total_rooms")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  // Foreign Key
  def addressFk = foreignKey(
    "fk_hotel_address",                  // name
    hotelAddressId,                      // source column
    TableQuery[Addresses] // referenced table
  )(_.addressId,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade
  )


  // Unique constraint on hotel name (optional)
  def uniqueHotelName = index("idx_unique_hotel_name", hotelName, unique = true)

  // Index for better query performance
  def idxAssignedOwner = index("idx_assigned_owner", assignedOwnerId)
  def idxCreatedAt = index("idx_created_at", createdAt)

  def * = (
    hotelId,
    hotelName,
    hotelAddressId,
    assignedOwnerId,
    totalFloors,
    totalRooms,
    createdAt,
    updatedAt
  ).<>((Hotel.apply _).tupled, Hotel.unapply)
}
