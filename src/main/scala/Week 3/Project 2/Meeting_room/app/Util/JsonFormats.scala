// app/utils/JsonFormats.scala
package Util

import Models._
import play.api.libs.json._

import java.sql.Timestamp
import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait JsonFormats {

  implicit val uuidFormat: Format[UUID] = new Format[UUID] {
    def reads(json: JsValue): JsResult[UUID] = json match {
      case JsString(s) =>
        try JsSuccess(UUID.fromString(s))
        catch { case _: IllegalArgumentException => JsError("Invalid UUID format") }
      case _ => JsError("Expected string for UUID")
    }

    def writes(uuid: UUID): JsValue = JsString(uuid.toString)
  }

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsNumber(ts.getTime)
    def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsNumber(n) => JsSuccess(new Timestamp(n.toLong))
      case _ => JsError("Expected number for timestamp")
    }
  }
  // UserRole formats
  implicit val userRoleFormat: Format[UserRole] = new Format[UserRole] {
    def reads(json: JsValue): JsResult[UserRole] = json match {
      case JsString("SUPERADMIN") => JsSuccess(UserRole.SuperAdmin)
      case JsString("SYSTEMADMIN") => JsSuccess(UserRole.SystemAdmin)
      case JsString("ADMINSTAFF") => JsSuccess(UserRole.AdminStaff)
      case JsString("EMPLOYEE") => JsSuccess(UserRole.Employee)
      case JsString("ROOMSERVICETEAM") => JsSuccess(UserRole.RoomServiceTeam)
      case _ => JsError("Invalid user role")
    }

    def writes(role: UserRole): JsValue = JsString(role.toString)
  }

  // User formats
  implicit val userFormat: Format[User] = Json.format[User]
}

object JsonFormats extends JsonFormats