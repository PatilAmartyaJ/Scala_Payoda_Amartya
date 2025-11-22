
package Requests

import play.api.libs.json.{Json, Reads}

case class CreateGuestRequest (first_name: String,
                               last_name: String,
                               email: String,
                               phone: String,
                               gender: String,
                               Id_type: String,
                               Id_issue_country: String,
                               address_Id: String,
                               nationalities: Seq[String])

object CreateGuestRequest {
  implicit val reads: Reads[CreateGuestRequest] = Json.reads[CreateGuestRequest]
}



