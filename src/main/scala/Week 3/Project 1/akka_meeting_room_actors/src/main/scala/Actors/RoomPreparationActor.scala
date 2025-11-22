package Actors

import Actors.BookingConfirmationActor.SendConfirmationEmail
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import spray.json.JsValue

object RoomPreparationActor {

  sealed trait Command

  case class SendRoomPreperationEmail(email: String, employeeName: String, roomName: String, roomLocation: String, startTime: String) extends Command

  def apply(): Behavior[Command] = {

    Behaviors.receive { (ctx, msg) =>
      msg match {
        case SendRoomPreperationEmail(email, employeeName, roomName, roomLocation, startTime) =>
          ctx.log.info(s"Please prepare the room ! ${email}")

          val message =
            s"""
               |Dear $employeeName,
               |
               |Room Preparation Required:
               |
               |Room: $roomName
               |Location: $roomLocation
               |Time: $startTime
               |
               |Thank you,
               |Office Management System
               |""".stripMargin.trim

          println(message)
          //          emailService.sendEmail(
          //            ev.guestEmail,
          //            "Welcome to Our Hotel",
          //            body
          //          )

          Behaviors.same
      }
    }
  }
}
