package Actors

import Actors.RoomPreparationActor.{Command, SendRoomPreperationEmail}
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object ScheduleReminderActor {
  sealed trait Command

  case class SendRoomPreperationEmail(email: String, employeeName: String, roomName: String, roomLocation: String, startTime: String) extends Command

  def apply(): Behavior[Command] = {

    Behaviors.receive { (ctx, msg) =>
      msg match {
        case SendRoomPreperationEmail(email, employeeName, roomName, roomLocation, startTime) =>
          ctx.log.info(s"Please prepare the room ! ${email}")
          val message =
            s"""Reminder: $employeeName meeting starts in 15 minutes
               |        Room: $roomName
               |        Location: $roomLocation
               |
               |        Time: ${startTime}""".stripMargin.trim

          println(message)
          Behaviors.same
      }
    }
  }
}