package Tables


import Models.{Room, User, UserRole}
import slick.jdbc.MySQLProfile.api._

import java.sql.Timestamp
import java.time.LocalDateTime

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def user_id = column[String]("user_id", O.PrimaryKey)
  def username = column[String]("username", O.Unique)
  def email = column[String]("email", O.Unique)
  def passwordHash = column[String]("password_hash")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def department = column[String]("department")
  def employeeId = column[String]("employee_id", O.Unique)
  def phone = column[Option[String]]("phone")
  def role = column[String]("role")
  def isActive = column[Boolean]("is_active")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")
  def lastLogin = column[Option[Timestamp]]("last_login")

  def * = (
    user_id,
    username,
    email,
    passwordHash,
    firstName,
    lastName,
    department,
    employeeId,
    phone,
    role,
    isActive,
    createdAt,
    updatedAt,
    lastLogin
  ).<>((User.apply _).tupled, User.unapply)

}
