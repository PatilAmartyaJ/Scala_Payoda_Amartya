package services


  import models.{CheckIn, Guest, Room, RoomType}

  import javax.inject.{Inject, Singleton}
  import java.util.Properties
  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}

  import scala.concurrent.{ExecutionContext, Future, Promise}
  import play.api.libs.json.{Format, JsString, JsValue, Json, OFormat, Writes}
  import repositories.{CheckInRepository, GuestRepository, HotelRepository, RoomRepository, RoomTypeRepository}

@Singleton
class KafkaProducerService @Inject()(
                                      checkInRepo: CheckInRepository,
                                      guestRepo: GuestRepository,
                                      hotelRepo: HotelRepository,
                                      roomRepo: RoomRepository,
                                      roomTypeRepo: RoomTypeRepository,
                                      roomService: RoomService
                                    )(implicit ec: ExecutionContext) {


//      val props = new Properties()
//      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sys.env.get("BROKER_HOST").getOrElse("localhost")+":9092")
//      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
//      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
//      new KafkaProducer[String, String](props)
//
//     val topic=""
//
//    def send[T](key: String, event: T)(implicit writes: Writes[T]): Future[RecordMetadata] = {
//      val promise = Promise[RecordMetadata]()
//      val value = Json.stringify(Json.toJson(event))
//      val record = new ProducerRecord[String, String](topic, key, value)
//
//      producer.send(record, (metadata: RecordMetadata, exception: Exception) => {
//        if (exception != null) promise.failure(exception)
//        else promise.success(metadata)
//      })
//
//      promise.future
//    }
//
//    def close(): Unit = producer.close()
implicit val timestampFormat: Format[java.sql.Timestamp] = new Format[java.sql.Timestamp] {
  def writes(ts: java.sql.Timestamp) = JsString(ts.toString)
  def reads(json: JsValue) = json.validate[String].map(java.sql.Timestamp.valueOf)
}

  implicit val bookingFormat: OFormat[CheckIn] = Json.format[CheckIn]
  implicit val guestFormat: OFormat[Guest]     = Json.format[Guest]
  implicit val roomFormat: OFormat[Room]       = Json.format[Room]
  implicit val categoryFormat: OFormat[RoomType] = Json.format[RoomType]

  // ---------------------------------------
  // Kafka config
  // ---------------------------------------
  private val topic = "hotel_notifications"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  // ---------------------------------------
  // MAIN LOGIC
  // ---------------------------------------
  def processBookingEvent(checkInId: String): Future[Either[String, JsValue]] =
    for {
      checkInOpt <- checkInRepo.findById(checkInId)
      result <- checkInOpt match {

        case None =>
          Future.successful(Left("Booking not found"))

        case Some(booking) =>
          for {
            guestOpt <- guestRepo.findById(booking.guestId)
            roomOpt  <- roomRepo.findById(booking.roomId)
            roomCategoryOpt <- roomOpt
              .map(room => roomTypeRepo.findById(room.roomTypeId))
              .getOrElse(Future.successful(None))

          } yield {

            if (guestOpt.isEmpty) Left("Guest not found")
            else if (roomOpt.isEmpty) Left("Room not found")
            else if (roomCategoryOpt.isEmpty) Left("Room category not found")
            else {

              val guest = guestOpt.get
              val room = roomOpt.get
              val category = roomCategoryOpt.get

              // Final Kafka JSON payload
              val payload = Json.obj(
                "type" -> "BOOKING_CREATED",
                "booking" -> Json.toJson(booking),
                "guest" -> Json.toJson(guest),
                "room" -> Json.obj(
                  "id" -> room.roomId,
                  "roomNumber" -> room.roomNumber,
                  "floor" -> room.roomFloor,
                  "category" -> Json.toJson(category)
                ),
                "services" -> Json.obj(
                  "roomServiceWelcomeEmail" -> true,
                  "wifiCredentialsEmail"    -> true,
                  "restaurantMenuEmails"    -> true
                )
              )
              println(payload)
              producer.send(new ProducerRecord[String, String](topic, payload.toString()))
              Right(payload)
            }
          }
      }
    } yield result

}
