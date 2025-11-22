package tables

import models.{Address, Guest, Room}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json

import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp


class Guests(tag: Tag) extends Table[Guest](tag, "guests") {




  // JSON mapping for Seq[String]
  implicit val seqStringColumnType = MappedColumnType.base[Seq[String], String](seq => Json.toJson(seq).toString(), str => Json.parse(str).as[Seq[String]])

  def guestId = column[String]("guest_id", O.PrimaryKey)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def fullName = column[String]("full_name")
  def email = column[String]("email", O.Unique)
  def phone = column[String]("phone", O.Unique)
  def gender = column[String]("gender")
  def idType = column[String]("id_type")
  def idIssueCountry = column[String]("id_issue_country")
  def guestAddressId = column[String]("guest_address_id")
  def nationalities = column[Seq[String]]("nationalities", O.SqlType("JSON"))
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (
    guestId,
    firstName,
    lastName,
    fullName,
    email,
    phone,
    gender,
    idType,
    idIssueCountry,
    guestAddressId,
    nationalities,
    createdAt,
    updatedAt
  ).<>((Guest.apply _).tupled, Guest.unapply)
}
