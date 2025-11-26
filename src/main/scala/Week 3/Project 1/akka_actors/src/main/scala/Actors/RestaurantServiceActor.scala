package Actors

import Models.BookingEvent
import Services.{EmailHelper, WeeklyMenu}
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.time.LocalDate

object RestaurantServiceActor {

  sealed trait Command

  case class StartMenuService(email: String, fullName:String,roomNo: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case StartMenuService(email,fullName, roomNo) =>
          val today = LocalDate.now.getDayOfWeek

          val menuText = WeeklyMenu.menuFor(today.toString)

          val subject=s"Daily menu event for $fullName"

          val body=
            s"""
               |Dear ${fullName},
               |To: $email
               |Room: $roomNo
               |📧 RESTAURANT MENU for today:
               |${menuText}
               |""".stripMargin
          EmailHelper.sendEmail(email,subject,body)

          Behaviors.same
      }
    }
}

/*
* package service

import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import akka.actor.typed.Behavior
import scala.concurrent.duration._
import scala.BookingJsonFormat._

object RestaurantServiceActor {



  def apply(): Behavior[Command] =
    Behaviors.withTimers { timers =>
      Behaviors.receive { (context, message) =>

        message match {
          case StartDailyMenu(evt) =>
            val guest = evt.guest
            timers.startTimerWithFixedDelay(
              evt.booking.id,
              SendMenu(evt.booking.id, guest.email, guest.fullName),
              24.hours
            )
            context.log.info(s"[RestaurantService] Daily menu emails started for booking ${evt.booking.id}")
            Behaviors.same

          case StopDailyMenu(id) =>
            timers.cancel(id)
            context.log.info(s"[RestaurantService] Daily menu emails stopped for $id")
            Behaviors.same

          case SendWelcomeMenu(evt) =>
            val guest = evt.guest

            val body =
              s"""
                 |Hello ${guest.fullName},
                 |
                 |🍽️ Welcome to our Hotel Restaurant!
                 |
                 |Your stay includes full access to our buffet dining services.
                 |
                 |⏰ Dining Timings:
                 | • Breakfast: 8:00 AM – 10:00 AM
                 | • Lunch:     1:00 PM – 3:00 PM
                 | • Dinner:    8:00 PM – 10:00 PM
                 |
                 |🥗 Today's Menu Highlights:
                 | • Breakfast: Idli, Dosa, Upma
                 | • Lunch: Paneer Butter Masala, Veg Biryani, Naan
                 | • Dinner: Dal Tadka, Jeera Rice, Aloo Gobi
                 |
                 |We hope you enjoy your stay and dining with us!
                 |
                 |Warm Regards,
                 |Restaurant Team
                 |""".stripMargin

            EmailHelper.sendEmail(guest.email, "Restaurant Welcome", body)
            context.log.info(s"[RestaurantService] Welcome restaurant email sent for booking ${evt.booking.id}")
            Behaviors.same

          case SendMenu(_, email, fullName) =>
            val body =
              s"""
                 |Hello $fullName,
                 |
                 |Today's Menu:
                 | - Breakfast: Idli / Dosa
                 | - Lunch: Paneer Butter Masala
                 | - Dinner: Dal Tadka with Jeera Rice
                 |
                 |Regards,
                 |Restaurant Team
                 |""".stripMargin

            EmailHelper.sendEmail(email, "Today's Menu", body)
            Behaviors.same
        }
      }
    }
}

* */
