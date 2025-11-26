package Services

import akka.actor.{ActorSystem, Cancellable}
import scala.concurrent.duration._
import java.time.{LocalTime, ZoneId, ZonedDateTime}
import scala.concurrent.ExecutionContext

object DailyScheduler {

  def scheduleDailyAt(
                       time: LocalTime
                     )(task: => Unit)(implicit system: ActorSystem, ec: ExecutionContext): Cancellable = {

    val zone = ZoneId.systemDefault()
    val now: ZonedDateTime = ZonedDateTime.now(zone)

    val todayAtTime = now.`with`(time)

    val firstRun =
      if (now.isAfter(todayAtTime)) todayAtTime.plusDays(1)
      else todayAtTime

    val initialDelay = java.time.Duration.between(now, firstRun).toMillis.millis

    system.scheduler.scheduleWithFixedDelay(
      initialDelay,
      24.hours
    )(() => task)
  }
}

