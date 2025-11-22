package tables

import models.Address
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp

class Addresses(tag: Tag) extends Table[Address](tag, "addresses") {
  def addressId = column[String]("address_id", O.PrimaryKey)
  def addressLine1 = column[String]("address_line1")
  def addressLine2 = column[Option[String]]("address_line2")
  def city = column[String]("city")
  def state = column[Option[String]]("state")
  def country = column[String]("country")
  def postalCode = column[Option[String]]("postal_code")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (
    addressId,
    addressLine1,
    addressLine2,
    city,
    state,
    country,
    postalCode,
    createdAt,
    updatedAt
  ).<>((Address.apply _).tupled, Address.unapply)
}