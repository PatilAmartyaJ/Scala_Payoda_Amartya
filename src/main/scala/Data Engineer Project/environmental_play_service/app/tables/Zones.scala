package tables

import models.Zone
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp

class Zones(tag: Tag) extends Table[Zone](tag, "zone") {
  def zoneId    = column[String]("zone_id", O.PrimaryKey)
  def name      = column[String]("name")
  def city      = column[String]("city")
  def latitude  = column[Double]("latitude")
  def longitude = column[Double]("longitude")
  def createdAt = column[java.sql.Timestamp]("created_at")

  def * = (
    zoneId, name, city, latitude, longitude, createdAt
  ).<>((Zone.apply _).tupled, Zone.unapply)
}

