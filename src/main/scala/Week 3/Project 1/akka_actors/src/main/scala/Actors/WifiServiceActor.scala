package Actors

import Models.CheckInConfirmed
import Services.EmailHelper
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object WifiServiceActor {
  sealed trait Command
  case class SendWifiCredentials(email: String, fullName:String,roomNumber:String, wifiUser:String,wifiPassword: String) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case SendWifiCredentials(email,fullName,roomNumber,wifiUser,wifiPassword) =>
       val  subject=s"Sending WiFi credentials to ${fullName}"

        val wifiCredentials = s"""
                                 |Dear ${fullName},
                                 |
                                 |Your WiFi credentials for room ${roomNumber}:
                                 |
                                 |Network: Hotel-Guest
                                 |Username: ${wifiUser}
                                 |Password: ${wifiPassword}
                                 |
                                 |Best regards,
                                 |WiFi Service Team
        """.stripMargin

        EmailHelper.sendEmail(email, subject, wifiCredentials)
        Behaviors.same
    }
  }


}

