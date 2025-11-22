package Actors

import Models.CheckInConfirmed
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object WifiServiceActor {
  sealed trait Command
  case class SendWifiCredentials(email: String, fullName:String,roomNumber:String, wifiUser:String,wifiPassword: String) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case SendWifiCredentials(email,fullName,roomNumber,wifiUser,wifiPassword) =>
        context.log.info(s"Sending WiFi credentials to ${email}")
        println(s"📶 WIFI ACTOR TRIGGERED for $email")     // <-- ADD THIS

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

        context.log.info(s"WiFi credentials sent to ${email}")

        Behaviors.same
    }
  }

  private def generateWifiPassword(bookingId: String): String = {
    s"Hotel${bookingId.take(4).toUpperCase}"
  }
}

