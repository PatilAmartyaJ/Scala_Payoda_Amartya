package Actors


import Models.CheckInConfirmed
import Services.EmailService
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object RoomServiceActor {

  sealed trait Command
  case class SendWelcomeEmail(email:String, roomType:String) extends Command

  def apply(): Behavior[Command] = {

    Behaviors.receive { (ctx, msg) =>

      msg match {
        case SendWelcomeEmail(email,roomType) =>
          ctx.log.info(s"Sending welcome email to ${email}")

          val body =
            s"""
               |Welcome to our Hotel!
               |Room Category: ${roomType}
               |
               |Emergency Contact: 999999999
               |Room Service: 111-222-333
               |
               |We wish you a pleasant stay.
               |""".stripMargin
          println(body)
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

