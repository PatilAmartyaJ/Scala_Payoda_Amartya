
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext

object SensorSimulatorApp {
  def main(args: Array[String]): Unit = {
    // Load configuration
    val config = ConfigFactory.load()

    val kafkaBootstrapServers = config.getString("kafka.bootstrapServer")
    val topic = config.getString("kafka.topic")

    // Create Actor System
    implicit val system: ActorSystem[Nothing] = ActorSystem(
      Behaviors.empty,
      "SensorSimulatorSystem"
    )

    implicit val ec: ExecutionContext = system.executionContext

    // Start the simulator
    val simulator = system.systemActorOf(
      SensorSimulator(kafkaBootstrapServers, topic),
      "sensorSimulator"
    )

    // Add shutdown hook
    sys.addShutdownHook {
      println("Shutting down sensor simulator...")
      system.terminate()
    }

    // Keep the application running
    println(s"Sensor Simulator started! Sending data to Kafka: $kafkaBootstrapServers, topic: $topic")
    println("Press Ctrl+C to stop...")

    // Keep main thread alive
    while (true) {
      Thread.sleep(1000)
    }
  }
}