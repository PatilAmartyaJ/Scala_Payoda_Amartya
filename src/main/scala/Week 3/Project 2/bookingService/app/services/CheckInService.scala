package services

import models.{CheckIn, Room}
import repositories.{CheckInRepository, GuestRepository, HotelRepository}
import util.IDGenerator
import util.IDGenerator.IdType

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// Kafka
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

@Singleton
class CheckInService @Inject()(
                                checkInRepo: CheckInRepository,
                                guestRepo: GuestRepository,
                                hotelRepo: HotelRepository,
                                roomService: RoomService
                              )(implicit ec: ExecutionContext) {

  def checkIn(
               hotelId: String,
               roomNumber: String,
               guestId: String,
               numberOfGuests: Int,
               depositAmount: Option[BigDecimal] = None,
               expectedCheckInTime: Option[Timestamp] = None,
               idDocumentType: Option[String] = None,
               idDocumentNumber: Option[String] = None,
               remarks: Option[String] = None,
               handledByStaffId: Option[String] = None
             ): Future[CheckIn] = {

    val now = new Timestamp(System.currentTimeMillis())
    val checkInId = IDGenerator.generate(IdType.CheckIn)

    // Step 1: Verify hotel exists
    hotelRepo.findById(hotelId).flatMap {
      case Some(_) =>
        // Step 2: Verify guest exists
        guestRepo.findById(guestId).flatMap {
          case Some(_) =>
            // Step 3: Mark room as booked
            roomService.markAsBooked(hotelId, roomNumber, guestId).flatMap { room: Room =>

              // Step 4: Create CheckIn record
              val checkIn = CheckIn(
                checkInId = checkInId,
                bookingId = None,
                hotelId = hotelId,
                roomId = room.roomId,
                roomTypeId = room.roomTypeId,
                guestId = guestId,
                actualCheckInTime = now,
                expectedCheckInTime = expectedCheckInTime,
                numberOfGuests = numberOfGuests,
                depositAmount = depositAmount,
                idDocumentType = idDocumentType,
                idDocumentNumber = idDocumentNumber,
                remarks = remarks,
                handledByStaffId = handledByStaffId,
                status = "Confirmed",
                createdAt = now,
                updatedAt = now
              )
              checkInRepo.create(checkIn)
              // Step 5: Save to repository
//              checkInRepo.create(checkIn).map { savedCheckIn =>
//                // Step 6: Produce Kafka message
//                val topic = "hotel-booking-events"
//                val message =
//                  s"""{
//                     "checkInId": "${savedCheckIn.checkInId}",
//                     "hotelId": "$hotelId",
//                     "roomId": "${savedCheckIn.roomId}",
//                     "guestId": "$guestId",
//                     "timestamp": "$now"
//                   }"""
//                val record = new ProducerRecord[String, String](topic, savedCheckIn.checkInId, message)
//                kafkaProducer.send(record)
//
//                savedCheckIn
//              }
            }

          case None =>
            Future.failed(new IllegalArgumentException(s"Guest with ID $guestId does not exist"))
        }

      case None =>
        Future.failed(new IllegalArgumentException(s"Hotel with ID $hotelId does not exist"))
    }
  }
}
