package Requests

import play.api.libs.json.{Json, Reads}

case class CreateRoomRequest(roomNumber: String, roomType: String, roomFloor:Int)
object CreateRoomRequest {
  implicit val reads: Reads[CreateRoomRequest] = Json.reads[CreateRoomRequest]
}
