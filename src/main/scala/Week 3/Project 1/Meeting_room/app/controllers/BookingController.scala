package controllers

// app/controllers/BookingController.scala


import Models.Booking
import Repositories.UserRepository
import Requests.CreateBookingRequest
import Services.{BookingConflict, BookingService, BookingStatistics}
import models._

import javax.inject._
import play.api.libs.json._
import play.api.mvc.Results.logger
import play.api.mvc._

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Singleton
class BookingController @Inject()(
                                   cc: ControllerComponents,
                                   bookingService: BookingService,
                                   userRepo: UserRepository
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import Util.JsonFormats._
  implicit val bookingReads: Reads[Booking] = Json.reads[Booking]
  implicit val bookingWrites: Writes[Booking] = Json.writes[Booking]
  implicit val bookingFormat: Format[Booking] = Json.format[Booking]
  implicit def seqBookingWrites: Writes[Seq[Booking]] = Writes.seq(bookingFormat)
  implicit val createBookingRequestFormat: Format[CreateBookingRequest] = Json.format[CreateBookingRequest]
  implicit val bookingConflictFormat: Format[BookingConflict] = Json.format[BookingConflict]
  implicit val bookingStatisticsFormat: Format[BookingStatistics] = Json.format[BookingStatistics]
  private val logger = play.api.Logger(this.getClass)


  // Add this to your BookingService or create a temporary debug method
  def debugCheckUsers(bookedBy: String, employeeId: String): Future[Unit] = {
    println(s"DEBUG: Checking if users exist in database...")

    for {
      adminUser <- userRepo.findById(bookedBy)
      employeeUser <- userRepo.findById(employeeId)

      // Also check if any users exist at all
      allUsers <- userRepo.findAll()
    } yield {
      println(s"DEBUG: Admin user '$bookedBy' exists: ${adminUser.isDefined}")
      adminUser.foreach(user => println(s"DEBUG: Admin user details: $user"))

      println(s"DEBUG: Employee user '$employeeId' exists: ${employeeUser.isDefined}")
      employeeUser.foreach(user => println(s"DEBUG: Employee user details: $user"))

      println(s"DEBUG: All users in database: ${allUsers.size}")
      allUsers.foreach(user => println(s"DEBUG: User: ${user.user_id} - ${user.username} - ${user.role}"))
    }
  }
    def createBooking() = Action.async(parse.json) { implicit request =>
      try {
        // Manual JSON parsing to avoid format issues
        val roomLocation = (request.body \ "roomLocation").as[String]
        val bookedBy = (request.body \ "bookedBy").as[String].trim
        val employeeId = (request.body \ "employeeId").as[String].trim
        val title = (request.body \ "title").as[String]
        val description = (request.body \ "description").asOpt[String]
        val purpose = (request.body \ "purpose").as[String]
        val department = (request.body \ "department").as[String]
        val expectedDuration = (request.body \ "expectedDuration").as[Int]

        debugCheckUsers(bookedBy,employeeId)
        // Parse timestamps manually
        val startTime = parseTimestampFromJson(request.body \ "startTime")
        val endTime = parseTimestampFromJson(request.body \ "endTime")

        val bookingRequest = CreateBookingRequest(
          roomLocation, bookedBy, employeeId, title, description,
          purpose, department, startTime, endTime, expectedDuration
        )

        bookingService.createBooking(bookingRequest).map {
          case Right(createdBooking) =>
             logger.info(s"Booking created successfully: ${createdBooking.booking_id}")
             Created(Json.toJson(createdBooking))
          case Left(error) =>
            logger.warn(s"Failed to create booking: $error")
            BadRequest(Json.obj("error" -> error))
        }.recover {
          case ex: Exception =>
            logger.error("Error creating booking", ex)
            InternalServerError(Json.obj("error" -> "Internal server error"))
        }

      } catch {
        case ex: Exception =>
          logger.error(s"Invalid booking request: ${ex.getMessage}")
          Future.successful(BadRequest(Json.obj("error" -> s"Invalid request: ${ex.getMessage}")))
      }
    }

  private def parseTimestampFromJson(js: JsLookupResult): Timestamp = {
    js.asOpt[Long] match {
      case Some(millis) => new Timestamp(millis)
      case None =>
        js.asOpt[String] match {
          case Some(dateString) =>
            try {
              val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
              val localDateTime = LocalDateTime.parse(dateString, formatter)
              Timestamp.valueOf(localDateTime)
            } catch {
              case _: Exception =>
                throw new IllegalArgumentException(s"Invalid date format: $dateString. Use milliseconds or 'yyyy-MM-dd HH:mm:ss'")
            }
          case None =>
            throw new IllegalArgumentException("Missing or invalid timestamp")
        }
    }
  }
  def cancelBooking(bookingId: String) = Action.async { request =>
    val cancelledBy: String = request.headers.get("user-id").getOrElse("UNKNOWN")

    bookingService.cancelBooking(bookingId, cancelledBy).map {
      case Right(true) =>
        Ok(Json.obj("message" -> "Booking cancelled successfully"))
      case Right(false) =>
        BadRequest(Json.obj("error" -> "Failed to cancel booking"))
      case Left(error) =>
        BadRequest(Json.obj("error" -> error))
    }.recover {
      case ex: Exception =>

        InternalServerError(Json.obj("error" -> "Internal server error"))
    }
  }


  def checkAvailability(roomId: String, startTime: String, endTime: String) = Action.async {
    try {
      val start: Timestamp = Timestamp.valueOf(startTime)
      val end: Timestamp   = Timestamp.valueOf(endTime)

      bookingService.checkRoomAvailability(roomId, start, end).map { available =>
        Ok(Json.obj("available" -> available))
      }
    } catch {
      case _: IllegalArgumentException =>
        Future.successful(BadRequest(Json.obj("error" -> "Invalid timestamp format")))
      case ex: Exception =>
        Future.successful(InternalServerError(Json.obj("error" -> "Internal server error")))
    }
  }

  def getUpcomingBookings = Action.async {
    bookingService.getUpcomingBookings().map { bookings =>
      Ok(Json.toJson(bookings))
    }
  }
}

