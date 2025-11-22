package Requests

import java.sql.Timestamp
import play.api.libs.json._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class CreateBookingRequest(
                                 roomLocation: String,
                                 bookedBy: String,
                                 employeeId: String,
                                 title: String,
                                 description: Option[String] = None,
                                 purpose: String,
                                 department: String,
                                 startTime: Timestamp,
                                 endTime: Timestamp,
                                 expectedDuration: Int
                               )

object CreateBookingRequest {

  // Custom Timestamp format that handles both numbers and strings
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {

    // Reading from JSON
    override def reads(json: JsValue): JsResult[Timestamp] = {
      json match {
        case JsNumber(millis) =>
          JsSuccess(new Timestamp(millis.longValue))

        case JsString(dateString) =>
          try {
            // Try different date formats
            val formatters = List(
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
              DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
              DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )

            val timestamp = formatters.foldLeft[Option[Timestamp]](None) { (result, formatter) =>
              result.orElse {
                try {
                  Some(Timestamp.valueOf(LocalDateTime.parse(dateString, formatter)))
                } catch {
                  case _: Exception => None
                }
              }
            }.getOrElse(throw new Exception(s"Invalid date format: $dateString"))

            JsSuccess(timestamp)
          } catch {
            case e: Exception =>
              JsError(s"Invalid timestamp format: ${e.getMessage}")
          }

        case _ =>
          JsError("Expected number or string for timestamp")
      }
    }

    // Writing to JSON
    override def writes(timestamp: Timestamp): JsValue = {
      JsNumber(timestamp.getTime)
    }
  }

  // CreateBookingRequest format MUST be defined AFTER timestampFormat
  implicit val format: Format[CreateBookingRequest] = Json.format[CreateBookingRequest]
}