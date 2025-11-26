package Actors


import Models.CheckInConfirmed
import Services.EmailHelper
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object RoomServiceActor {

  sealed trait Command

  case class SendWelcomeEmail(email: String, fullName:String, roomNumber:String,roomType: String) extends Command
  case class SendThankYouEmail(email: String, fullName:String) extends Command
  def apply(): Behavior[Command] = {

    Behaviors.receive { (ctx, msg) =>

      msg match {
        case SendWelcomeEmail(email, fullName,roomNumber,roomType) =>{
          val subject=s"Welcome Email to ${fullName}"

          val body =
            s"""
               |Dear ${fullName},
               |Welcome to our Hotel!
               |Room Number: ${roomNumber}
               |Booked Room Category: ${roomType}
               |
               |Emergency Contact: 999999999
               |Room Service: 111-222-333
               |
               |We wish you a pleasant stay.
               |""".stripMargin
          println(body)
          EmailHelper.sendEmail(email,subject,body)
          //          emailService.sendEmail(
          //            ev.guestEmail,
          //            "Welcome to Our Hotel",
          //            body
          //          )

          Behaviors.same
        }
        case SendThankYouEmail(email,fullName)=>{
          val subject=s"Thank you email to ${email}"

          val body =
            s"""
               | Dear ${fullName}
               | Thank you so much, Visit Us Again
               |  ----------------------------------------------------
               |                        Example Inn
               |                   Luxury • Comfort • Elegance
               |  ----------------------------------------------------
               |
               |  Address : 123 Test Street, Block B, Apt 101
               |  Phone   : +91 9307321519
               |  Email   : amartya@ExampleInn.com
               |  Website : www.ExampleInn.com
               |
               |  Front Desk Manager : Mr. Amartya Patil
               |  Phone (Direct)     : +91 9307321519
               |
               |  ----------------------------------------------------
               |                "Your comfort is our priority."
               |  ----------------------------------------------------
               |
               |""".stripMargin
          println(body)
          EmailHelper.sendEmail(email,subject,body)
          Behaviors.same
        }

      }
    }
  }
}


//}
