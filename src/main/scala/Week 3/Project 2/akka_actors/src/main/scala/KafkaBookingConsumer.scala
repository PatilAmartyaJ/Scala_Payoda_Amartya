import Actors.{RestaurantServiceActor, RoomServiceActor, WifiServiceActor, ServiceCoordinatorActor}
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object RootBehavior {
  sealed trait Command
}

object KafkaBookingConsumer {

  private val Topic = "hotel_notifications"

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[RootBehavior.Command] =
      ActorSystem(createRootBehavior(), "BookingConsumerSystem")
  }

  // ------------------------------
  // Define the root behavior type
  // ------------------------------
  def createRootBehavior(): Behavior[RootBehavior.Command] = Behaviors.setup { ctx =>

    // Spawn children
    val roomActor       = ctx.spawn(RoomServiceActor(), "RoomServiceActor")
    val wifiActor       = ctx.spawn(WifiServiceActor(), "WifiServiceActor")
    val restaurantActor = ctx.spawn(RestaurantServiceActor(), "RestaurantServiceActor")

    val coordinator =
      ctx.spawn(ServiceCoordinatorActor(roomActor, wifiActor, restaurantActor), "ServiceCoordinator")

    // Start Kafka Consumer
    runKafkaConsumer(coordinator)(ctx.system)

    Behaviors.empty
  }

  // ------------------------------
  // Kafka Consumer Stream
  // ------------------------------
  def runKafkaConsumer(
                        coordinator: akka.actor.typed.ActorRef[ServiceCoordinatorActor.Command]
                      )(implicit system: ActorSystem[_]): Unit = {

    implicit val ec: ExecutionContext = system.executionContext

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("booking-consumer-group-v2")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(Topic))
      .mapAsync(1) { msg =>

        val raw = msg.value()

        println(s"\n📥 Kafka Message Received:\n$raw\n")

        try {
          val js = raw.parseJson.asJsObject
          println(js)
          coordinator ! ServiceCoordinatorActor.ProcessBookingEvent(js)
        } catch {
          case e: Exception =>
            println(s"⚠️ Skipping non-JSON message: $raw")
        }

        Future.successful(())
      }
      .runWith(Sink.ignore)

  }
}
