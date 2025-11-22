package Requests

import play.api.libs.json.{Json, Reads}

case class CreateAddressRequest (addressLine1: String,
                                 addressLine2: Option[String],
                                 city: String,
                                 state: Option[String],
                                 country: String,
                                 postalCode: Option[String])

object CreateAddressRequest {
  implicit val reads: Reads[CreateAddressRequest] = Json.reads[CreateAddressRequest]
}

