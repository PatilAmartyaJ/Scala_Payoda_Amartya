//package Actors
//
//import Services.{WeeklyMenu,EmailHelper}
//import akka.actor.typed.{ActorRef, Behavior}
//import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
//
//import java.time.{DayOfWeek, LocalDate, LocalDateTime, LocalTime}
//import scala.concurrent.duration._
//import scala.util.{Failure, Success}
//
//
//
//class RestaurantServiceActor2(
//                              context: akka.actor.typed.scaladsl.ActorContext[RestaurantServiceActor2.Command],
//                              timers: TimerScheduler[RestaurantServiceActor2.Command],
//                              emailHelper: EmailHelper
//                            ) {
//  import RestaurantServiceActor2._
//a
//  // Start the daily scheduler when actor is created
//  startDailyScheduler()
//
//  private def startDailyScheduler(): Unit = {
//    val initialDelay = calculateInitialDelay()
//    val interval = 24.hours
//
//    timers.startTimerWithFixedDelay(SendScheduledMenus, initialDelay, interval)
//    context.log.info(s"Daily menu scheduler started. First run in: $initialDelay")
//  }
//
//  private def calculateInitialDelay(): FiniteDuration = {
//    val now = LocalDateTime.now()
//    val targetTime = LocalTime.of(8, 0) // 8:00 AM
//
//    var nextRun = now.withHour(targetTime.getHour)
//      .withMinute(targetTime.getMinute)
//      .withSecond(0)
//      .withNano(0)
//
//    // If it's already past 8 AM today, schedule for tomorrow 8 AM
//    if (now.isAfter(nextRun)) {
//      nextRun = nextRun.plusDays(1)
//    }
//
//    val delaySeconds = java.time.Duration.between(now, nextRun).getSeconds
//    delaySeconds.seconds
//  }
//
//  def running(scheduledGuests: Map[String, (String, String)]): Behavior[Command] = {
//    Behaviors.receive { (ctx, msg) =>
//      msg match {
//        case StartMenuService(email, fullName, roomNo) =>
//          ctx.log.info(s"Immediate menu email sent to: $email")
//          sendMenuEmail(email, fullName, roomNo)
//          Behaviors.same
//
//        case ScheduleDailyMenu(email, fullName, roomNo) =>
//          ctx.log.info(s"Scheduling daily menu for: $email")
//          val updatedGuests = scheduledGuests + (email -> (fullName, roomNo))
//          running(updatedGuests)
//
//        case StopScheduling(email) =>
//          ctx.log.info(s"Stopping daily menu for: $email")
//          val updatedGuests = scheduledGuests - email
//          running(updatedGuests)
//
//        case SendScheduledMenus =>
//          ctx.log.info(s"Sending daily menus to ${scheduledGuests.size} guests")
//
//          scheduledGuests.foreach { case (email, (fullName, roomNo)) =>
//            ctx.log.info(s"Sending menu to: $email")
//            sendMenuEmail(email, fullName, roomNo)
//          }
//
//          Behaviors.same
//
//        case MenuSendResult(email, success) =>
//          if (success) {
//            ctx.log.info(s"Menu email successfully sent to: $email")
//          } else {
//            ctx.log.error(s"Failed to send menu email to: $email")
//          }
//          Behaviors.same
//      }
//    }
//  }
//
//  private def sendMenuEmail(email: String, fullName: String, roomNo: String): Unit = {
//    import context.executionContext
//
//    val today = LocalDate.now().getDayOfWeek
//    val menuText = WeeklyMenu.menuFor(today.toString)
//
//    val subject = s"Daily menu event for $fullName"
//    val body =
//      s"""
//         |Dear ${fullName},
//         |To: $email
//         |Room: $roomNo
//         |📧 RESTAURANT MENU for today:
//         |${menuText}
//         |""".stripMargin
//
//    // Send email asynchronously
//    emailHelper.sendEmail(email, subject, body).onComplete {
//      case Success(success) =>
//        context.self ! MenuSendResult(email, success)
//      case Failure(exception) =>
//        context.log.error(s"Email sending failed for $email: ${exception.getMessage}")
//        context.self ! MenuSendResult(email, false)
//    }
//  }
//}
//
//object RestaurantServiceActor2 {
//
//  sealed trait Command
//  case class StartMenuService(email: String, fullName: String, roomNo: String) extends Command
//  case class ScheduleDailyMenu(email: String, fullName: String, roomNo: String) extends Command
//  case class StopScheduling(email: String) extends Command
//  private case object SendScheduledMenus extends Command
//  private case class MenuSendResult(email: String, success: Boolean) extends Command
//
//  def apply(emailHelper: EmailHelper): Behavior[Command] = {
//    Behaviors.setup { context =>
//      Behaviors.withTimers { timers =>
//        new RestaurantServiceActor2(context, timers, emailHelper).running(Map.empty)
//      }
//    }
//  }
//}