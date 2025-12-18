package controllers

import DTOs.ZoneRequest
import Services.{DashboardSummaryService, SensorReadingByZoneService, ZoneService}
import models.{SensorReadingByZone, Zone}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.Future
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import com.datastax.oss.driver.api.core.CqlSession
class ZoneController @Inject()(
                               cc: ControllerComponents,
                               zoneService:ZoneService ,
                               sensorReadingService: SensorReadingByZoneService,
                               dashboardService:DashboardSummaryService
                             )(implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val zoneWrites: Writes[Zone] = Json.writes[Zone]

  def createZone(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[ZoneRequest].fold(
      errors => Future.successful(
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid input", "details" -> errors.toString))
      ),
      zoneData => {
        zoneService.createZone(zoneData.name, zoneData.city, zoneData.latitude, zoneData.longitude).map { zone =>
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
  def getAllZones: Action[AnyContent] = Action.async {
    zoneService.getAllZones().map { zones =>
      Ok(Json.obj(
        "status" -> "success",
        "zones" -> Json.toJson(zones)
      ))
    }.recover {
      case e =>
        e.printStackTrace()
        InternalServerError(Json.obj(
          "status" -> "error",
          "message" -> "Failed to fetch hotels"
        ))
    }
  }

  implicit val sensorReadingWrites: Writes[SensorReadingByZone] = Json.writes[SensorReadingByZone]

  def getRecentRecordsByZone(zoneId: String, limit: Option[String]) = Action { implicit request =>
    try {
      val limitValue = limit.map(_.toInt).getOrElse(50)   // <-- FIX: Convert String → Int

      val readings = sensorReadingService.getByZone(zoneId, limitValue)
      Ok(Json.toJson(readings))
    } catch {
      case ex: Exception =>
        InternalServerError(Json.obj(
          "error" -> "Failed to fetch sensor readings",
          "message" -> ex.getMessage
        ))
    }
  }

  def getDailySummary(zoneId:String)= Action{ implicit request =>
    try {

      val readings = dashboardService.fetchTodaySummary(zoneId)
      Ok(Json.toJson(readings))
    } catch {
      case ex: Exception =>
        InternalServerError(Json.obj(
          "error" -> "Failed to fetch sensor readings",
          "message" -> ex.getMessage
        ))
    }
  }

}
