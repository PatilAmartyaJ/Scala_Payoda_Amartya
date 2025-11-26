package controllers

import Models._
import Services.{BookingService, KafkaService, NotificationService, UserService}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaController @Inject()(
                                 cc: ControllerComponents,
                                 kafkaProducerService: KafkaService,
                                 bookingService: BookingService,
                                 notificationService: NotificationService,
                                 userService: UserService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import Util.JsonFormats._

  def sendBookingConfirmation: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "bookingId").asOpt[String] match {
      case Some(checkInId) =>
        kafkaProducerService.processBookingEvent(checkInId).map {
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




  def sendRoomPreparationAlert: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "bookingId").asOpt[String] match {
      case Some(bookingId) =>
        bookingService.getBookingById(bookingId).flatMap {
          case Some(booking) =>
            notificationService.notifyRoomServiceTeam(booking).map { notifications =>
              Ok(Json.obj(
                "status" -> "success",
                "message" -> "Room preparation alerts sent successfully",
                "notificationsSent" -> notifications.size
              ))
            }
          case None =>
            Future.successful(NotFound(Json.obj(
              "status" -> "error",
              "message" -> "Booking not found"
            )))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing 'bookingId' in request body"
        )))
    }
  }

  def sendReminderAlert: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "bookingId").asOpt[String] match {
      case Some(bookingId) =>
        bookingService.getBookingById(bookingId).flatMap {
          case Some(booking) =>
            notificationService.sendReminder(booking).map {
              case notification =>
                Ok(Json.obj(
                  "status" -> "success",
                  "message" -> "Reminder alert sent successfully",
                  "notificationId" -> notification.notification_id
                ))
            }
          case None =>
            Future.successful(NotFound(Json.obj(
              "status" -> "error",
              "message" -> "Booking not found"
            )))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing 'bookingId' in request body"
        )))
    }
  }

  def sendReleaseNotification: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "bookingId").asOpt[String] match {
      case Some(bookingId) =>
        bookingService.getBookingById(bookingId).flatMap {
          case Some(booking) =>
            notificationService.sendReleaseNotification(booking).map { notifications =>
              Ok(Json.obj(
                "status" -> "success",
                "message" -> "Release notifications sent successfully",
                "notificationsSent" -> notifications.size
              ))
            }
          case None =>
            Future.successful(NotFound(Json.obj(
              "status" -> "error",
              "message" -> "Booking not found"
            )))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing 'bookingId' in request body"
        )))
    }
  }

  def sendCustomNotification: Action[JsValue] = Action.async(parse.json) { request =>
    val notificationType = (request.body \ "type").asOpt[String]
    val recipientId = (request.body \ "recipientId").asOpt[String]
    val subject = (request.body \ "subject").asOpt[String]
    val message = (request.body \ "message").asOpt[String]
    val bookingId = (request.body \ "bookingId").asOpt[String]

    (notificationType, recipientId, subject, message, bookingId) match {
      case (Some(nType), Some(recId), Some(subj), Some(msg), Some(bId)) =>
        // Get user email first
        userService.getUserById(recId).flatMap {
          case Some(user) =>
            val customNotification = Notification.create(
              notification_type = nType,
              recipientId = recId,
              recipientEmail = user.email,
              subject = subj,
              message = msg,
              bookingId = bId,
              status = NotificationStatus.Pending.toString,
              scheduledFor = new Timestamp(System.currentTimeMillis())
            )

            kafkaProducerService.sendNotification(customNotification).map { _ =>
              Ok(Json.obj(
                "status" -> "success",
                "message" -> "Custom notification sent successfully",
                "notificationId" -> customNotification.notification_id
              ))
            }

          case None =>
            Future.successful(NotFound(Json.obj(
              "status" -> "error",
              "message" -> "Recipient user not found"
            )))
        }

      case _ =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing required fields: type, recipientId, subject, message, bookingId"
        )))
    }
  }

//  def sendDirectNotification: Action[JsValue] = Action.async(parse.json) { request =>
//    val notificationType = (request.body \ "type").asOpt[String]
//    val recipientEmail = (request.body \ "recipientEmail").asOpt[String]
//    val subject = (request.body \ "subject").asOpt[String]
//    val message = (request.body \ "message").asOpt[String]
//    val bookingId = (request.body \ "bookingId").asOpt[String]
//
//    (notificationType, recipientEmail, subject, message) match {
//      case (Some(nType), Some(recEmail), Some(subj), Some(msg)) =>
//        // For direct notifications, we don't need recipientId, just email
//        // Create a temporary ID for the notification (since we don't have a user ID)
//        val tempRecipientId = java.util.UUID.randomUUID().toString
//
//        val directNotification = Notification.create(
//          notification_type = nType,
//          recipientId = tempRecipientId,
//          recipientEmail = recEmail,
//          subject = subj,
//          message = msg,
//          bookingId = bookingId,
//          status = NotificationStatus.Pending.toString,
//          scheduledFor = System.currentTimeMillis()
//        )
//
//        kafkaProducerService.sendNotification(directNotification).map { _ =>
//          Ok(Json.obj(
//            "status" -> "success",
//            "message" -> "Direct notification sent successfully",
//            "notificationId" -> directNotification.notification_id
//          ))
//        }
//
//      case _ =>
//        Future.successful(BadRequest(Json.obj(
//          "status" -> "error",
//          "message" -> "Missing required fields: type, recipientEmail, subject, message"
//        )))
//    }
//  }

  def getNotificationStatus: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "notificationId").asOpt[String] match {
      case Some(notificationId) =>
        kafkaProducerService.getNotificationStatus(notificationId).map {
          case Some(status) =>
            Ok(Json.obj(
              "status" -> "success",
              "notificationId" -> notificationId,
              "kafkaStatus" -> status
            ))
          case None =>
            NotFound(Json.obj(
              "status" -> "error",
              "message" -> "Notification not found in Kafka queue"
            ))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Missing 'notificationId' in request body"
        )))
    }
  }

//  def bulkSendNotifications: Action[JsValue] = Action.async(parse.json) { request =>
//    (request.body \ "notifications").asOpt[List[JsValue]] match {
//      case Some(notificationsJson) =>
//        val notificationFutures = notificationsJson.map { notificationJson =>
//          val nType = (notificationJson \ "type").asOpt[String]
//          val recipientId = (notificationJson \ "recipientId").asOpt[String]
//          val subject = (notificationJson \ "subject").asOpt[String]
//          val message = (notificationJson \ "message").asOpt[String]
//          val bookingId = (notificationJson \ "bookingId").asOpt[String]
//
//          (nType, recipientId, subject, message) match {
//            case (Some(nType), Some(recId), Some(subj), Some(msg)) =>
//              userService.getUserById(recId).map {
//                case Some(user) =>
//                  Some(Notification.create(
//                    notification_type = nType,
//                    recipientId = recId,
//                    recipientEmail = user.email,
//                    subject = subj,
//                    message = msg,
//                    bookingId = bookingId,
//                    status = NotificationStatus.Pending.toString,
//                    scheduledFor = System.currentTimeMillis()
//                  ))
//                case None =>
//                  None
//              }
//            case _ => Future.successful(None)
//          }
//        }
//
//        Future.sequence(notificationFutures).flatMap { notifications =>
//          val validNotifications = notifications.flatten
//          Future.sequence(
//            validNotifications.map(notification => kafkaProducerService.sendNotification(notification))
//          ).map { _ =>
//            Ok(Json.obj(
//              "status" -> "success",
//              "message" -> s"${validNotifications.size} notifications sent successfully",
//              "totalProcessed" -> notificationsJson.size,
//              "successful" -> validNotifications.size
//            ))
//          }
//        }
//
//      case None =>
//        Future.successful(BadRequest(Json.obj(
//          "status" -> "error",
//          "message" -> "Missing 'notifications' array in request body"
//        )))
//    }
//  }
}