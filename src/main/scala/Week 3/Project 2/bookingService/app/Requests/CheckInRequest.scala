package Requests
import play.api.libs.json._
import java.sql.Timestamp

case class CheckInRequest(
                           roomNumber: String,
                           guestId: String,
                           numberOfGuests: Int,
                           depositAmount: Option[BigDecimal] = None,
                           expectedCheckInTime: Option[Long] = None, // epoch millis
                           idDocumentType: Option[String] = None,
                           idDocumentNumber: Option[String] = None,
                           remarks: Option[String] = None,
                           handledByStaffId: Option[String] = None
                         )

object CheckInRequest {
  implicit val reads: Reads[CheckInRequest] = Json.reads[CheckInRequest]
  implicit val writes: OWrites[CheckInRequest] = Json.writes[CheckInRequest]
}
