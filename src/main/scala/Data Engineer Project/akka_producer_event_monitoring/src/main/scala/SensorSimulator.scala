

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.SendProducer

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import spray.json._

import scala.concurrent.duration._
import scala.util.Random

object SensorSimulator {

  def apply(kafkaBootstrapServers: String, topic: String): Behavior[Command] =
    Behaviors.setup { context =>

      val producerSettings = ProducerSettings(
        context.system,
        new StringSerializer,
        new StringSerializer
      ).withBootstrapServers(kafkaBootstrapServers)

      val producer = SendProducer(producerSettings)(context.system)

      // Simulate multiple zones and sensors
      val zones =List(
      "ZN-41HR9VatcUzl",
      "ZN-8bcEkmLdy6cM",
      "ZN-9vOmGAJuWYYA",
      "ZN-hgOhcvR3W1xc",
      "ZN-qOO9POi55dOh",
      "ZN-THpTeOHdvRy3",
      "ZN-V3Vd9d65x0K4",
      "ZN-vlBb3LdjmPcS"
      )

      val sensorsPerZone = 5

      Behaviors.withTimers { timers =>
        // Schedule readings every 10 seconds
        timers.startTimerWithFixedDelay(GenerateReadings, 10.seconds)

        running(producer, topic, zones, sensorsPerZone)
      }
    }

  private def running(
                       producer: SendProducer[String, String],
                       topic: String,
                       zones: List[String],
                       sensorsPerZone: Int
                     ): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case GenerateReadings =>
        zones.foreach { zoneId =>
          (1 to sensorsPerZone).foreach { sensorNum =>
            val reading = generateSensorReading(zoneId)

            val json = reading.toJson(SensorReadingJsonProtocol.sensorReadingFormat).compactPrint
            val record = new ProducerRecord[String, String](topic, json)

            producer.send(record).foreach { _ =>
              context.log.info(s"Sent reading: ${reading.sensorId} -> Zone $zoneId")
            }(context.executionContext)
          }
        }
        Behaviors.same
    }
  }

  private def generateSensorReading(zoneId: String): SensorReading = {
    val basePM25 = 10.0 + Random.nextDouble() * 40.0  // 10-50 µg/m³
    val basePM10 = 20.0 + Random.nextDouble() * 80.0  // 20-100 µg/m³
    val baseCO2 = 350.0 + Random.nextDouble() * 250.0 // 350-600 ppm

    // Add random fluctuations ±5%
    val fluctuation = 1.0 + (Random.nextDouble() * 0.1 - 0.05)

    // 2-3% chance of anomaly
    val hasAnomaly = Random.nextInt(100) < 3

    val pm25 = if (hasAnomaly) basePM25 * 2.5 else basePM25 * fluctuation
    val pm10 = if (hasAnomaly) basePM10 * 2.5 else basePM10 * fluctuation
    val co2Ppm = if (hasAnomaly) baseCO2 * 1.8 else baseCO2 * fluctuation

    SensorReading.create(

      zoneId = zoneId,
      pm25 = math.max(0, pm25),
      pm10 = math.max(0, pm10),
      co2Ppm = math.max(0, co2Ppm),
      deviceStatus = if (hasAnomaly) "WARNING" else "OK"
    )
  }

  sealed trait Command
  case object GenerateReadings extends Command
}