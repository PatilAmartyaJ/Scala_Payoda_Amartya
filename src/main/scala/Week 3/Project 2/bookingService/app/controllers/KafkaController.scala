package controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.{HotelService, KafkaProducerService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaController @Inject()(
                                 cc: ControllerComponents,
                                 hotelService: HotelService,
                                 kafkaProdService:KafkaProducerService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def sendBookingEvent: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "checkInId").asOpt[String] match {
      case Some(checkInId) =>
        kafkaProdService.processBookingEvent(checkInId).map {
          case Right(msg) =>
            Ok(Json.obj(
              "status" -> "success",
              "message" -> "Kafka message sent successfully",
              "kafkaPayload" -> msg
            ))
          case Left(error) =>
            NotFound(Json.obj(
              "status" -> "error",
              "message" -> error
            ))
        }

      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing 'checkInId' in request body"
        )))
    }
  }


}
