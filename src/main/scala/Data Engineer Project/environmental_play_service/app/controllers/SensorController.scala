package controllers

import DTOs.{SensorRequest, ZoneRequest}
import Services.SensorService
import models.{Sensor, Zone}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SensorController @Inject()(
                                cc: ControllerComponents,
                                sensorService:SensorService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val sensorWrites: Writes[Sensor] = Json.writes[Sensor]
  def createSensor(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[SensorRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      sensorData => {
        sensorService.createSensor(sensorData.zone_id, sensorData.sensor_type,sensorData.status).map { sensor =>
          Created(Json.toJson(sensor))
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
