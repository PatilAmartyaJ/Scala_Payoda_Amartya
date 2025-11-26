package Services

import Models.{Booking, BookingStatus, RoomAvailability, User, UserRole}
import Repositories.{BookingRepository, NotificationRepository, RoomRepository, UserRepository}
import Requests.CreateBookingRequest

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

@Singleton
class BookingService @Inject()(
                                bookingRepo: BookingRepository,
                                roomRepo: RoomRepository,
                                userRepo: UserRepository,
                                notificationRepo: NotificationRepository,
                                notificationService: NotificationService
                              )(implicit ec: ExecutionContext) {

  def createBooking(bookingRequest: CreateBookingRequest): Future[Either[String, Booking]] = {
    for {
      // Validate that the user making the booking is AdminStaff or SystemAdmin
      adminValidation <- validateAdminUser(bookingRequest.bookedBy)

      // Validate that the employee exists and is active
      employeeValidation <- validateEmployee(bookingRequest.employeeId)

      // Find available rooms at the specified location for the requested time
      availableRooms <- roomRepo.findAvailableRoomsByLocation(
        bookingRequest.roomLocation,
        bookingRequest.startTime,
        bookingRequest.endTime
      )

      result <- (adminValidation, employeeValidation, availableRooms) match {
        case (Right(adminUser), Right(employee), rooms)if rooms.nonEmpty =>
        // Use the first available room at the location
          val selectedRoom = rooms.head
          val booking = Booking.create(

            roomId = selectedRoom.room_id,
            bookedBy = bookingRequest.bookedBy,
            employeeId = bookingRequest.employeeId,
            title = bookingRequest.title,
            description = bookingRequest.description,
            purpose = bookingRequest.purpose,
            department = bookingRequest.department,
            startTime = bookingRequest.startTime,
            endTime = bookingRequest.endTime,
            expectedDuration = bookingRequest.expectedDuration,

          )

          for {
            createdBooking <- bookingRepo.create(booking)
            _ <- roomRepo.updateRoomAvailability(selectedRoom.room_id, RoomAvailability.Reserved)
//            _ <- notificationService.sendBookingConfirmation(createdBooking)
//            _ <- notificationService.notifyRoomServiceTeam(createdBooking)
          } yield Right(createdBooking)

        case (Left(adminError), _, _) =>
          Future.successful(Left(adminError))
        case (_, Left(employeeError), _) =>
          Future.successful(Left(employeeError))
        case (_, _, rooms) if rooms.isEmpty =>
          Future.successful(Left(s"No available rooms found at location: ${bookingRequest.roomLocation} for the requested time"))

      }
    } yield result
  }

  def cancelBooking(bookingId: String, cancelledBy: String): Future[Either[String, Boolean]] = {
    for {
      // Validate that the user cancelling has permission (admin or the original booker)
      permissionValidation <- validateCancellationPermission(bookingId, cancelledBy)

      result <- permissionValidation match {
        case Right((booking, hasPermission)) if hasPermission =>
          for {
            _ <- bookingRepo.updateStatus(bookingId, BookingStatus.Cancelled)
            _ <- roomRepo.updateRoomAvailability(booking.roomId, RoomAvailability.Available)
//            _ <- notificationService.sendCancellationNotification(booking, cancelledBy)
          } yield Right(true)

        case Right((_, false)) =>
          Future.successful(Left("You don't have permission to cancel this booking"))

        case Left(error) =>
          Future.successful(Left(error))
      }
    } yield result
  }

  def checkRoomAvailability(roomId: String, startTime: Timestamp, endTime: Timestamp): Future[Boolean] = {
    roomRepo.checkAvailability(roomId, startTime, endTime)
  }

  def getUpcomingBookings(): Future[Seq[Booking]] = {
    bookingRepo.findUpcomingBookings(30) // Next 30 minutes
  }

  def getBookingById(bookingId: String): Future[Option[Booking]] = {
    bookingRepo.findById(bookingId)
  }

  def getAllBookings(): Future[Seq[Booking]] = {
    bookingRepo.findAll()
  }

  def getBookingsByEmployee(employeeId: String): Future[Seq[Booking]] = {
    bookingRepo.findByEmployeeId(employeeId)
  }

  def getBookingsByAdmin(adminId: String): Future[Seq[Booking]] = {
    bookingRepo.findByBookedBy(adminId)
  }

  def getConflictingBookings(roomId: String, startTime: Timestamp, endTime: Timestamp): Future[Seq[Booking]] = {
    bookingRepo.findConflictingBookings(roomId, startTime, endTime)
  }

  def updateBookingStatus(bookingId: String, status: BookingStatus, updatedBy: String): Future[Either[String, Boolean]] = {
    for {
      adminValidation <- validateAdminUser(updatedBy)
      result <- adminValidation match {
        case Right(_) =>
          bookingRepo.updateStatus(bookingId, status).map(updated_rows =>
            if (updated_rows>0) Right(true) else Left("Booking not found")
          )
        case Left(error) =>
          Future.successful(Left(error))
      }
    } yield result
  }

//  def forceCancelBooking(bookingId: String, cancelledBy: String, reason: String): Future[Either[String, Boolean]] = {
//    for {
//      adminValidation <- validateSystemAdmin(cancelledBy)
//      result <- adminValidation match {
//        case Right(_) =>
//          for {
//            bookingOpt <- bookingRepo.findById(bookingId)
//            cancellationResult <- bookingOpt match {
//              case Some(booking) =>
//                for {
//                  _ <- bookingRepo.updateStatus(bookingId, BookingStatus.Cancelled)
//                  _ <- roomRepo.updateRoomAvailability(booking.roomId, RoomAvailability.Available)
//                  _ <- notificationService.sendForceCancellationNotification(booking, cancelledBy, reason)
//                } yield Right(true)
//              case None =>
//                Future.successful(Left("Booking not found"))
//            }
//          } yield cancellationResult
//        case Left(error) =>
//          Future.successful(Left(error))
//      }
//    } yield result
//  }

  // Validation methods
  private def validateAdminUser(userId: String): Future[Either[String, User]] = {
    userRepo.findById(userId).map {
      case Some(user) if user.isActive && isAdminUser(user) =>
        Right(user)
      case Some(user) if !user.isActive =>
        Left("User account is deactivated")
      case Some(user) =>
        Left(s"User ${user.username} does not have admin privileges to book rooms")
      case None =>
        Left("User not found")
    }
  }

  private def validateSystemAdmin(userId: String): Future[Either[String, User]] = {
    userRepo.findById(userId).map {
      case Some(user) if user.isActive && isSystemAdmin(user) =>
        Right(user)
      case Some(user) if !user.isActive =>
        Left("User account is deactivated")
      case Some(user) =>
        Left(s"User ${user.username} does not have system admin privileges")
      case None =>
        Left("User not found")
    }
  }

  private def validateEmployee(employeeId: String): Future[Either[String, User]] = {
    userRepo.findById(employeeId).map {
      case Some(user) if user.isActive =>
        Right(user)
      case Some(user) if !user.isActive =>
        Left("Employee account is deactivated")
      case None =>
        Left("Employee not found")
    }
  }

  private def validateCancellationPermission(bookingId: String, userId: String): Future[Either[String, (Booking, Boolean)]] = {
    for {
      bookingOpt <- bookingRepo.findById(bookingId)
      userOpt <- userRepo.findById(userId)
      result <- (bookingOpt, userOpt) match {
        case (Some(booking), Some(user)) =>
          val hasPermission =
            booking.bookedBy == userId ||
              booking.employeeId == userId ||
              isAdminUser(user)

          Future.successful(Right((booking, hasPermission)))
        case (None, _) =>
          Future.successful(Left("Booking not found"))
        case (_, None) =>
          Future.successful(Left("User not found"))
      }
    } yield result
  }

  private def isAdminUser(user: User): Boolean = {
    user.role == UserRole.AdminStaff.toString ||
      user.role == UserRole.SystemAdmin.toString ||
      user.role == UserRole.SuperAdmin.toString
  }

  private def isSystemAdmin(user: User): Boolean = {
    user.role == UserRole.SystemAdmin.toString ||
      user.role == UserRole.SuperAdmin.toString
  }

  // Room locking and conflict resolution
  def lockRoomForBooking(roomId: String, startTime: Timestamp, endTime: Timestamp, lockedBy: String): Future[Either[String, Boolean]] = {
    for {
      adminValidation <- validateAdminUser(lockedBy)
      availability <- roomRepo.checkAvailability(roomId, startTime, endTime)
      result <- (adminValidation, availability) match {
        case (Right(_), true) =>
          roomRepo.updateRoomAvailability(roomId, RoomAvailability.Reserved).map { updatedRows =>
            if (updatedRows > 0) Right(true) else Left("Failed to lock room")
          }
        case (Right(_), false) =>
          Future.successful(Left("Room is not available for the requested time slot"))
        case (Left(error), _) =>
          Future.successful(Left(error))
      }
    } yield result
  }

  def releaseRoomLock(roomId: String, releasedBy: String): Future[Either[String, Boolean]] = {
    for {
      adminValidation <- validateAdminUser(releasedBy)
      result <- adminValidation match {
        case Right(_) =>
          roomRepo.updateRoomAvailability(roomId, RoomAvailability.Available).map { updatedRows =>
            if (updatedRows > 0) Right(true) else Left("Failed to release room lock")
          }
        case Left(error) =>
          Future.successful(Left(error))
      }
    } yield result
  }


  def getBookingConflicts(): Future[Seq[BookingConflict]] = {
    val now = System.currentTimeMillis()
    for {
      activeBookings <- bookingRepo.findActiveBookings()
      conflicts <- Future.sequence(
        activeBookings.map { booking =>
          bookingRepo.findConflictingBookings(booking.roomId, booking.startTime, booking.endTime)
            .map(_.filter(_.booking_id != booking.booking_id))
            .map(conflicting => BookingConflict(booking, conflicting))
        }
      )
    } yield conflicts.filter(_.conflictingBookings.nonEmpty)
  }
}

// Request/Response case classes

case class BookingConflict(
                            originalBooking: Booking,
                            conflictingBookings: Seq[Booking]
                          )

case class BookingStatistics(
                              totalBookings: Int,
                              activeBookings: Int,
                              cancelledBookings: Int,
                              mostBookedRoom: Option[String],
                              bookingSuccessRate: Double
                            )
