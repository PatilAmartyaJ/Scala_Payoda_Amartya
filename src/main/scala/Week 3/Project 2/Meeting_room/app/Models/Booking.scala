// app/models/Booking.scala
package Models

import Util.IDGenerator
import Util.IDGenerator.IdType

import java.sql.Timestamp

case class Booking(
                    booking_id: String,
                    roomId: String, // Foreign key to Room
                    bookedBy: String, // Foreign key to User (Admin staff)
                    employeeId: String, // Foreign key to User (Employee)
                    title: String,
                    description: Option[String] = None,
                    purpose: String,
                    department: String,
                    startTime: Timestamp,
                    endTime: Timestamp,
                    expectedDuration: Int,
                    status: String,
                    createdAt: Timestamp,
                    updatedAt: Timestamp
                  )

object Booking {
  def create(
  roomId: String,
  bookedBy: String, // Admin staff user ID
  employeeId: String, // Employee user ID for whom the room is booked
  title: String,
  description: Option[String] = None,
  purpose: String,
  department: String,
  startTime: Timestamp, // milliseconds
  endTime: Timestamp,   // milliseconds
  expectedDuration: Int
            ):Booking={
    val now=new Timestamp(System.currentTimeMillis())
    Booking(
      booking_id = IDGenerator.generate(IdType.Booking),
      roomId = roomId,
      bookedBy = bookedBy,
      employeeId = employeeId,
      title = title,
      description = description,
      purpose = purpose,
      department = department,
      startTime = startTime,
      endTime = endTime,
      expectedDuration = expectedDuration,
      status = BookingStatus.Confirmed.toString,
      createdAt = now, updatedAt = now
    )
  }
}

sealed trait MeetingPurpose {
  def toString: String
}

object MeetingPurpose {
  case object TeamMeeting extends MeetingPurpose {
    override def toString: String = "TEAM_MEETING"
  }
  case object ClientMeeting extends MeetingPurpose {
    override def toString: String = "CLIENT_MEETING"
  }
  case object Presentation extends MeetingPurpose {
    override def toString: String = "PRESENTATION"
  }
  case object Training extends MeetingPurpose {
    override def toString: String = "TRAINING"
  }
}

sealed trait BookingStatus {
  def toString: String
}

object BookingStatus {
  case object Confirmed extends BookingStatus {
    override def toString: String = "CONFIRMED"
  }

  case object Cancelled extends BookingStatus {
    override def toString: String = "CANCELLED"
  }

  case object Completed extends BookingStatus {
    override def toString: String = "COMPLETED"
  }

  case object InProgress extends BookingStatus {
    override def toString: String = "IN_PROGRESS"
  }

  case object NoShow extends BookingStatus {
    override def toString: String = "NO_SHOW"
  }

  case object Released extends BookingStatus {
    override def toString: String = "RELEASED"
  }
}