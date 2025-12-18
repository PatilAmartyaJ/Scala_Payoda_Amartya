package controllers

import Services.DailyPollutionSummaryService
import models.DailyPollutionSummary
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DailyPollutionSummaryController @Inject()(
                                                 cc: ControllerComponents,
                                                 dailyPollutionSummaryService:DailyPollutionSummaryService
                                               )(implicit ec: ExecutionContext) extends AbstractController(cc){
  implicit val pollutionThresholdWrites: Writes[DailyPollutionSummary] = Json.writes[DailyPollutionSummary]
  def getByDateAndZone(date: String, zoneId: String): Action[AnyContent] = Action.async {
    // Parse date string to java.sql.Date
    val sqlDate = java.sql.Date.valueOf(date)

    dailyPollutionSummaryService.getByDateAndZone(sqlDate, zoneId).map {
      case Some(summary) =>
        Ok(Json.obj(
          "status" -> "success",
          "data" -> Json.toJson(summary)
        ))
      case None =>
        NotFound(Json.obj(
          "status" -> "not_found",
          "message" -> s"No pollution summary found for date $date and zone $zoneId"
        ))
    }.recover {
      case e: IllegalArgumentException =>
        BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Invalid date format. Please use YYYY-MM-DD format."
        ))
      case e =>
        e.printStackTrace()
        InternalServerError(Json.obj(
          "status" -> "error",
          "message" -> "Failed to fetch pollution summary data"
        ))
    }
  }
  def getByZone(zoneId: String): Action[AnyContent] = Action.async {
    // Parse date string to java.sql.Date
    val sqlDate = java.sql.Date.valueOf(LocalDate.now().minusDays(7))

    dailyPollutionSummaryService.getByDateAndZone(sqlDate, zoneId).map {
      case Some(summary) =>
        Ok(Json.obj(
          "status" -> "success",
          "data" -> Json.toJson(summary)
        ))
      case None =>
        NotFound(Json.obj(
          "status" -> "not_found",
          "message" -> s"No pollution summary found for date $sqlDate and zone $zoneId"
        ))
    }.recover {
      case e: IllegalArgumentException =>
        BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Invalid date format. Please use YYYY-MM-DD format."
        ))
      case e =>
        e.printStackTrace()
        InternalServerError(Json.obj(
          "status" -> "error",
          "message" -> "Failed to fetch pollution summary data"
        ))
    }
  }
}
