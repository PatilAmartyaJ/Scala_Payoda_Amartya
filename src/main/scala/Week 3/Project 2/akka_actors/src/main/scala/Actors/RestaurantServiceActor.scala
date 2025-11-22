package Actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object RestaurantServiceActor {

  sealed trait Command
  case class StartMenuService(email: String, roomNo: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case StartMenuService(email, roomNo) =>
          ctx.log.info(s"[RestaurantService] Starting daily menu service for $email")

          println(
            s"""
               |📧 RESTAURANT MENU
               |To: $email
               |Room: $roomNo
               |Today's Menu: Soup, Roti, Paneer, Rice, Dessert
               |""".stripMargin
          )

          Behaviors.same
      }
    }
}
