package Services


import Models.{Booking, Notification, Room, User}
import Repositories.{BookingRepository, RoomRepository, UserRepository}

import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{Format, JsString, JsValue, Json, OFormat}

import java.util.UUID

@Singleton
class KafkaService @Inject()(bookingRepo:BookingRepository,userRepo:UserRepository,roomRepo:RoomRepository)(implicit ec: ExecutionContext) {


  implicit val timestampFormat: Format[java.sql.Timestamp] = new Format[java.sql.Timestamp] {
    def writes(ts: java.sql.Timestamp) = JsString(ts.toString)
    def reads(json: JsValue) = json.validate[String].map(java.sql.Timestamp.valueOf)
  }

  implicit val bookingFormat: OFormat[Booking] = Json.format[Booking]
  implicit val userFormat: OFormat[User]     = Json.format[User]
  implicit val roomFormat: OFormat[Room]       = Json.format[Room]

  // ---------------------------------------
  // Kafka config
  // ---------------------------------------
  private val topic = "meeting_notifications"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  // ---------------------------------------
  // MAIN LOGIC
  // ---------------------------------------
  def processBookingEvent(bookingId: String): Future[Either[String, JsValue]] =
    for {
      checkInOpt <- bookingRepo.findById(bookingId)
      result <- checkInOpt match {

        case None =>
          Future.successful(Left("Booking not found"))

        case Some(booking) =>
          for {
            userOpt <- userRepo.findById(booking.employeeId)
            roomOpt  <- roomRepo.findById(booking.roomId)

          } yield {

            if (userOpt.isEmpty) Left("Guest not found")
            else if (roomOpt.isEmpty) Left("Room not found")
             else {

              val user = userOpt.get
              val room = roomOpt.get

              // Final Kafka JSON payload
              val payload = Json.obj(
                "type" -> "BOOKING_CREATED",
                "booking" -> Json.toJson(booking),
                "user" -> Json.toJson(user),
                "room" -> Json.obj(
                  "id" -> room.room_id,
                  "roomName" -> room.name,
                  "location" -> room.location,
                  "facilities" -> Json.toJson(room.facilities)
                ),
//                {
//                  "booking_id": "BK-N1Z42",
//                  "roomId": "RM-P2Ev",
//                  "bookedBy": "GST-d9NXUKvODaXf",
//                  "employeeId": "GST-2XsEYsLe6J2Y",
//                  "title": "Weekly Team Meeting",
//                  "description": "Discuss weekly tasks and updates.",
//                  "purpose": "Team Coordination",
//                  "department": "IT",
//                  "startTime": 1763854200000,
//                  "endTime": 1763857800000,
//                  "expectedDuration": 60,
//                  "status": "CONFIRMED",
//                  "createdAt": 1763818421931,
//                  "updatedAt": 1763818421931
//                }
              )
              println(payload)
              producer.send(new ProducerRecord[String, String](topic, payload.toString()))
              Right(payload)
            }
          }
      }
    } yield result

  def sendNotification(notification: Notification): Future[Unit] = Future {
    val message = Json.obj(
      "eventId" -> UUID.randomUUID(),
      "notificationId" -> notification.notification_id.toString,
      "notification_type" -> notification.notification_type,
      "recipientEmail" -> notification.recipientEmail,
      "subject" -> notification.subject,
      "message" -> notification.message,
      "bookingId" -> notification.bookingId.toString
    ).toString()

    val record = new ProducerRecord[String, String](topic, notification.notification_id.toString, message)
    producer.send(record)
  }
  def getNotificationStatus(notificationId: String): Future[Option[String]] = {
    // Implementation to check Kafka message status
    // This could check if the message was successfully produced
    Future.successful(Some("delivered")) // Placeholder
  }



    def close(): Unit = producer.close()
}
