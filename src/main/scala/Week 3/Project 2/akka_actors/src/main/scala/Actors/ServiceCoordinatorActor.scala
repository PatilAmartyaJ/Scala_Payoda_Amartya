package Actors

import Actors.{RestaurantServiceActor, RoomServiceActor, WifiServiceActor}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import spray.json._
import spray.json.DefaultJsonProtocol._

object ServiceCoordinatorActor {

  sealed trait Command
  case class ProcessBookingEvent(eventJson: JsValue) extends Command

  def apply(
             roomService: ActorRef[RoomServiceActor.Command],
             wifiService: ActorRef[WifiServiceActor.Command],
             restaurantService: ActorRef[RestaurantServiceActor.Command]
           ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {

        case ProcessBookingEvent(eventJson) =>
          ctx.log.info("[Coordinator] Processing booking event")

          val root = eventJson.asJsObject

          val guest = root.fields("guest").asJsObject
          val room  = root.fields("room").asJsObject

          val email       = guest.fields("email").convertTo[String]
          val fullName    = guest.fields("full_name").convertTo[String]
          val guestId     = guest.fields("guest_id").convertTo[String]

          val roomNumber  = room.fields("roomNumber").convertTo[String]
          val roomId      = room.fields("id").convertTo[String]
          val roomType    = room.fields("category").asJsObject
          val roomTypeName= roomType.fields("name").convertTo[String]

          val wifiUser = s"user_$roomNumber"
          val wifiPass = "password123"

          ctx.log.info(
            s"[Coordinator] Guest: $fullName ($email), Room: $roomNumber, RoomID: $roomId"
          )

          roomService ! RoomServiceActor.SendWelcomeEmail(email, roomTypeName)
          wifiService ! WifiServiceActor.SendWifiCredentials(email, fullName, roomNumber, wifiUser, wifiPass)
          restaurantService ! RestaurantServiceActor.StartMenuService(email, roomNumber)

          Behaviors.same

      }
    }
}
