package Day6.Q6

import scala.io.StdIn
import java.sql.Connection

object TrafficApp {

  def main(args: Array[String]): Unit = {
    println("🚦 Smart Traffic Management System 🚦")

    // Connect to database
    val connOpt = DatabaseConnection.getConnection

    connOpt match {
      case Some(conn) =>
        try {
          var continue = true
          while (continue) {
            showMenu()
            val choice = StdIn.readInt()

            choice match {
              case 1 => addVehicle(conn)
              case 2 => addTrafficSignal(conn)
              case 3 => recordViolation(conn)
              case 4 => updateSignalStatus(conn)
              case 5 => TrafficOperations.getVehicles(conn)
              case 6 => TrafficOperations.getSignals(conn)
              case 7 => TrafficOperations.getViolations(conn)
              case 8 =>
                println("👋 Exiting system. Goodbye!")
                continue = false
              case _ => println("⚠️ Invalid choice. Please try again.")
            }
          }
        } finally {
          connOpt.foreach(_.close())
          println("🔒 Database connection closed.")
        }

      case None =>
        println("❌ Could not connect to the database. Exiting.")
    }
  }

  // ---------------- MENU DISPLAY ----------------
  def showMenu(): Unit = {
    println(
      s"""
         |==============================
         |  SMART TRAFFIC MANAGEMENT
         |==============================
         |1. Add Vehicle
         |2. Add Traffic Signal
         |3. Record Violation
         |4. Update Signal Status
         |5. View Vehicles
         |6. View Traffic Signals
         |7. View Violations
         |8. Exit
         |==============================
         |Enter your choice:
         |""".stripMargin)
  }

  // ---------------- OPERATIONS ----------------

  def addVehicle(conn: Connection): Unit = {
    println("Enter License Plate:")
    val license = StdIn.readLine()

    println("Enter Vehicle Type:")
    val vType = StdIn.readLine()

    println("Enter Owner Name:")
    val owner = StdIn.readLine()

    TrafficOperations.insertVehicle(conn, license, vType, owner)
  }

  def addTrafficSignal(conn: Connection): Unit = {
    println("Enter Signal Location:")
    val location = StdIn.readLine()

    println("Enter Signal Status (Red/Green/Yellow):")
    val status = StdIn.readLine()

    TrafficOperations.insertTrafficSignal(conn, location, status)
  }

  def recordViolation(conn: Connection): Unit = {
    println("Enter Vehicle License Plate:")
    val license = StdIn.readLine()

    println("Enter Signal Location:")
    val location = StdIn.readLine()

    println("Enter Violation Type (e.g., Signal Jump, Overspeeding):")
    val violation = StdIn.readLine()

    TrafficOperations.recordViolation(conn, license, location, violation)
  }

  def updateSignalStatus(conn: Connection): Unit = {
    println("Enter Signal Location:")
    val location = StdIn.readLine()

    println("Enter New Status (Red/Green/Yellow):")
    val newStatus = StdIn.readLine()

    TrafficOperations.updateSignalStatus(conn, location, newStatus)
  }
}
