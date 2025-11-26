package Actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.sql.Timestamp

object BookingConfirmationActor {
  sealed trait Command

  case class SendConfirmationEmail(email: String, employeeName: String, roomName: String, roomLocation: String, startTime: String, endTime: String) extends Command

  def apply(): Behavior[Command] = {

    Behaviors.receive { (ctx, msg) =>

      msg match {
        case SendConfirmationEmail(email, employeeName, roomName, roomLocation, startTime, endTime) =>
          ctx.log.info(s"Sending welcome email to ${email}")

          val message =
            s"""
               |Dear $employeeName,
               |
               |Your meeting room booking has been confirmed:
               |
               |Room: $roomName
               |Location: $roomLocation
               |Time: $startTime to $endTime
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
