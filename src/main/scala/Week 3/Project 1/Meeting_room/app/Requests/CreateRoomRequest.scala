package Requests

import Models.RoomFacility
import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue, Json}

case class CreateRoomRequest(
                              name: String,
                              location: String,
                              capacity: Int,
                              facilities: Seq[String]
                            )

object CreateRoomRequest {
  // Add RoomFacility format first

  // Then create the format for CreateRoomRequest
  implicit val format: Format[CreateRoomRequest] = Json.format[CreateRoomRequest]
}



