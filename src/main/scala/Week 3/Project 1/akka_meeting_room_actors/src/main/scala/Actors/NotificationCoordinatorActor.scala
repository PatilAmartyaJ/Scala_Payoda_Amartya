package Actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import spray.json.JsValue

import spray.json._
import spray.json.DefaultJsonProtocol._
object NotificationCoordinatorActor {
  sealed trait Command

  case class ProcessBookingEvent(eventJson: JsValue) extends Command

  def apply(
             bookingService: ActorRef[BookingConfirmationActor.Command],
                          roomprepService: ActorRef[RoomPreparationActor.Command],
             //             scheduleService: ActorRef[ScheduleReminderActor.Command]
           ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case ProcessBookingEvent(eventJson) =>
          ctx.log.info("[Coordinator] Processing booking event")

          val root = eventJson.asJsObject

          //          {
          //            "status": "success",
          //            "message": "Kafka message sent successfully",
          //            "kafkaPayload": {
          //              "type": "BOOKING_CREATED",
          //              "booking": {
          //              "booking_id": "BK-eYNgg",
          //              "roomId": "RM-wTjw",
          //              "bookedBy": "GST-d9NXUKvODaXf",
          //              "employeeId": "GST-2XsEYsLe6J2Y",
          //              "title": "Weekly Team Meeting",
          //              "description": "Discuss weekly tasks and updates.",
          //              "purpose": "Team Coordination",
          //              "department": "IT",
          //              "startTime": "2025-11-23 15:00:00.0",
          //              "endTime": "2025-11-23 16:00:00.0",
          //              "expectedDuration": 60,
          //              "status": "CONFIRMED",
          //              "createdAt": "2025-11-22 20:21:13.0",
          //              "updatedAt": "2025-11-22 20:21:13.0"
          //            },
          //              "user": {
          //              "user_id": "GST-2XsEYsLe6J2Y",
          //              "username": "sarah.smith",
          //              "email": "sarah.smith@example.com",
          //              "passwordHash": "a7e5927852e7191997bbc841701d181265be1e29cc959d744133e9cd517901bd",
          //              "firstName": "Sarah",
          //              "lastName": "Smith",
          //              "department": "HR",
          //              "employeeId": "HR90211",
          //              "role": "EMPLOYEE",
          //              "isActive": true,
          //              "createdAt": "2025-11-21 22:42:40.0",
          //              "updatedAt": "2025-11-21 22:42:40.0",
          //              "lastLogin": "2025-11-21 22:42:40.0"
          //            },
          //              "room": {
          //              "id": "RM-wTjw",
          //              "roomName": "Training Room",
          //              "location": "Block C",
          //              "facilities": [
          //              "WHITEBOARD",
          //              "AUDIOSYSTEM"
          //              ]
          //            }
          //            }
          //          }


          val booking = root.fields("booking").asJsObject
          val user = root.fields("user").asJsObject
          val room = root.fields("room").asJsObject

          val email = user.fields("email").convertTo[String]
          val fullName = user.fields("firstName").convertTo[String] + " " + user.fields("lastName").convertTo[String]
          val startTime = booking.fields("startTime").convertTo[String]
          val endTime = booking.fields("endTime").convertTo[String]

          val roomName = room.fields("roomName").convertTo[String]
          val roomId = room.fields("id").convertTo[String]
          val roomLocation = room.fields("location").convertTo[String]


          bookingService ! BookingConfirmationActor.SendConfirmationEmail(email, employeeName = fullName, roomName = roomName, roomLocation = roomLocation, startTime = startTime, endTime = endTime)
          roomprepService ! RoomPreparationActor.SendRoomPreperationEmail("amartyapatil879@gmail.com", "Amartya Patil", roomName = roomName, roomLocation = roomLocation, startTime = startTime)
          //          scheduleService ! ScheduleReminderActor.StartMenuService(email, roomNumber)

          Behaviors.same


      }
    }
}
