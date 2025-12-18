package controllers

import DTOs.{PollutionThresholdRequest, ZoneRequest}
import Services.{PollutionThresholdService, ZoneService}
import models.{PollutionThreshold, Zone}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import scala.concurrent.Future
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
class PollutionThresholdController @Inject()(
                                cc: ControllerComponents,
                                pollutionThresholdService:PollutionThresholdService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val pollutionThresholdWrites: Writes[PollutionThreshold] = Json.writes[PollutionThreshold]
  def createPollutionThreshold(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[PollutionThresholdRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      pollutionThreshold_data => {
        pollutionThresholdService.createPollutionThreshold(pollutionThreshold_data.zone_id,pollutionThreshold_data.pm2_5_limit,pollutionThreshold_data.pm10_limit,pollutionThreshold_data.co2_limit,pollutionThreshold_data.alert_level).map { zone =>
          Created(Json.toJson(zone))
        }.recover {
          case e: IllegalArgumentException => Conflict(Json.obj("status" -> "error", "message" -> e.getMessage))
          case e =>
            e.printStackTrace()
            InternalServerError(Json.obj("status" -> "error", "message" -> "Internal server error"))
        }
      }
    )
  }
}

