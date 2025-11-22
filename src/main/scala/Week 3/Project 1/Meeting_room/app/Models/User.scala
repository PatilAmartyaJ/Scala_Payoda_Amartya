package Models
// app/models/User.scala


import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

case class User(
                 user_id: String,
                 username: String,
                 email: String,
                 passwordHash: String,
                 firstName: String,
                 lastName: String,
                 department: String,
                 employeeId: String,
                 phone: Option[String] = None,
                 role: String,
                 isActive: Boolean = true,
                 createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
                 updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()),
                 lastLogin: Option[Timestamp] = None
               )
object User {
  def create(username: String,
             email: String,
             passwordHash: String,
             firstName: String,
             lastName: String,
             department: String,
             employeeId: String,
             phone: Option[String] = None,
             role: String,
             isActive: Boolean = true,
             lastLogin: Option[Timestamp] = None): User ={
     val now=new Timestamp(System.currentTimeMillis())
     User(
       user_id = IDGenerator.generate(IdType.User),
       username = username,
       email = email,
       passwordHash = passwordHash,
       firstName = firstName,
       lastName = lastName,
       department = department,
       employeeId = employeeId,
       phone = phone,
       role = role,
       isActive = isActive,
       createdAt = now, updatedAt = now,
       lastLogin = Some(now)
     )
  }
}
sealed trait UserRole {
  def toString: String
}
object UserRole {
  case object SuperAdmin extends UserRole{
    override def toString: String = "SUPERADMIN"
  }
  case object SystemAdmin extends UserRole{
    override def toString: String = "SYSTEMADMIN"
  }
  case object AdminStaff extends UserRole{
    override def toString: String = "ADMINSTAFF"
  }
  case object Employee extends UserRole{
    override def toString: String = "EMPLOYEE"
  }
  case object RoomServiceTeam extends UserRole{
    override def toString: String = "ROOMSERVICETEAM"
  }
}
