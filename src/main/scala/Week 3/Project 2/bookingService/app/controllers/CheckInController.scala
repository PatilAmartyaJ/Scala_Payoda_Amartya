package controllers

import Requests.CheckInRequest
import models.CheckIn
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{CheckInService, RoomService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CheckInController @Inject()(
                                   cc: ControllerComponents,
                                   checkInService: CheckInService,
                                   roomService: RoomService
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // once check in is done message will be send to kafka and then it should be consumed by akka actors

  //Already checked_in rooms will not be available for to check_in Again
  // Try to make seperate endpoint for avaialable rooms

  //
  def checkIn(hotelId: String): Action[JsValue] = Action.async(parse.json) { request =>
    // Parse JSON body
    request.body.validate[CheckInRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Invalid request",
          "details" -> JsError.toJson(errors)
        ))
      ),
      checkInData => {
        val expectedTime = checkInData.expectedCheckInTime.map(ts => new java.sql.Timestamp(ts))

        checkInService.checkIn(
          hotelId = hotelId,
          roomNumber = checkInData.roomNumber,
          guestId = checkInData.guestId,
          numberOfGuests = checkInData.numberOfGuests,
          depositAmount = checkInData.depositAmount,
          expectedCheckInTime = expectedTime,
          idDocumentType = checkInData.idDocumentType,
          idDocumentNumber = checkInData.idDocumentNumber,
          remarks = checkInData.remarks,
          handledByStaffId = checkInData.handledByStaffId
        ).map { checkIn: CheckIn =>

          Ok(Json.obj(
            "status" -> "success",
            "checkInId" -> checkIn.checkInId,
            "hotelId" -> checkIn.hotelId,
            "roomId" -> checkIn.roomId,
            "guestId" -> checkIn.guestId
          ))
        }.recover {
          case e: IllegalArgumentException =>
            NotFound(Json.obj("status" -> "error", "message" -> e.getMessage))
          case e =>
            e.printStackTrace()
            InternalServerError(Json.obj("status" -> "error", "message" -> "Internal server error"))
        }
      }
    )
  }
}

