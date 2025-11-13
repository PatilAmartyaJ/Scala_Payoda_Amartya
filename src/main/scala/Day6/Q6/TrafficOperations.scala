package Day6.Q6
import java.sql.{Connection, PreparedStatement, ResultSet, Timestamp}
import java.time.LocalDateTime

object TrafficOperations {

  // ---------- CREATE ----------
  def insertVehicle(conn: Connection, license: String, vehicleType: String, owner: String): Unit = {
    val query = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setString(1, license)
      stmt.setString(2, vehicleType)
      stmt.setString(3, owner)
      val rows = stmt.executeUpdate()
      println(s"✅ Vehicle inserted ($rows row affected)")
    } finally stmt.close()
  }

  def insertTrafficSignal(conn: Connection, location: String, status: String): Unit = {
    val query = "INSERT INTO TrafficSignals (location, status) VALUES (?, ?)"
    val stmt = conn.prepareStatement(query)
    try {
      stmt.setString(1, location)
      stmt.setString(2, status)
      val rows = stmt.executeUpdate()
      println(s"✅ Signal inserted ($rows row affected)")
    } finally stmt.close()
  }

  // ---------- READ ----------
  def getVehicles(conn: Connection): Unit = {
    val stmt = conn.prepareStatement("SELECT * FROM Vehicles")
    val rs = stmt.executeQuery()
    println("\n🚘 Vehicles:")
    while (rs.next()) {
      println(s"${rs.getInt("vehicle_id")}: ${rs.getString("license_plate")} | ${rs.getString("vehicle_type")} | ${rs.getString("owner_name")}")
    }
    rs.close(); stmt.close()
  }

  def getSignals(conn: Connection): Unit = {
    val stmt = conn.prepareStatement("SELECT * FROM TrafficSignals")
    val rs = stmt.executeQuery()
    println("\n🚦 Traffic Signals:")
    while (rs.next()) {
      println(s"${rs.getInt("signal_id")}: ${rs.getString("location")} - ${rs.getString("status")}")
    }
    rs.close(); stmt.close()
  }

  // ---------- UPDATE ----------
  def updateSignalStatus(conn: Connection, location: String, newStatus: String): Unit = {
    val stmt = conn.prepareStatement("UPDATE TrafficSignals SET status = ? WHERE location = ?")
    try {
      stmt.setString(1, newStatus)
      stmt.setString(2, location)
      val rows = stmt.executeUpdate()
      println(s"✅ Updated $rows signal(s) at $location to $newStatus")
    } finally stmt.close()
  }

  // ---------- DELETE ----------
  def deleteVehicle(conn: Connection, license: String): Unit = {
    val stmt = conn.prepareStatement("DELETE FROM Vehicles WHERE license_plate = ?")
    try {
      stmt.setString(1, license)
      val rows = stmt.executeUpdate()
      println(s"🗑️ Deleted $rows vehicle(s) with plate: $license")
    } finally stmt.close()
  }

  def deleteViolation(conn: Connection, violationId: Int): Unit = {
    val stmt = conn.prepareStatement("DELETE FROM Violations WHERE violation_id = ?")
    try {
      stmt.setInt(1, violationId)
      val rows = stmt.executeUpdate()
      println(s"🗑️ Deleted $rows violation(s) with ID: $violationId")
    } finally stmt.close()
  }

  // ---------- RECORD VIOLATION ----------
  def recordViolation(conn: Connection, license: String, signalLocation: String, violationType: String): Unit = {
    // Step 1: Find vehicle_id
    val vehicleStmt = conn.prepareStatement("SELECT vehicle_id FROM Vehicles WHERE license_plate = ?")
    vehicleStmt.setString(1, license)
    val vehicleRs = vehicleStmt.executeQuery()

    if (!vehicleRs.next()) {
      println(s"⚠️ Vehicle with plate $license not found!")
      vehicleRs.close(); vehicleStmt.close()
      return
    }

    val vehicleId = vehicleRs.getInt("vehicle_id")
    vehicleRs.close(); vehicleStmt.close()

    // Step 2: Find signal_id
    val signalStmt = conn.prepareStatement("SELECT signal_id FROM TrafficSignals WHERE location = ?")
    signalStmt.setString(1, signalLocation)
    val signalRs = signalStmt.executeQuery()

    if (!signalRs.next()) {
      println(s"⚠️ Signal at location $signalLocation not found!")
      signalRs.close(); signalStmt.close()
      return
    }

    val signalId = signalRs.getInt("signal_id")
    signalRs.close(); signalStmt.close()

    // Step 3: Insert violation
    val insertStmt = conn.prepareStatement(
      "INSERT INTO Violations (vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, ?)"
    )
    try {
      insertStmt.setInt(1, vehicleId)
      insertStmt.setInt(2, signalId)
      insertStmt.setString(3, violationType)
      insertStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()))
      insertStmt.executeUpdate()
      println(s"🚨 Violation recorded: $license at $signalLocation ($violationType)")
    } finally insertStmt.close()
  }

  // ---------- READ VIOLATIONS ----------
  def getViolations(conn: Connection): Unit = {
    val query =
      """SELECT v.violation_id, ve.license_plate, t.location, v.violation_type, v.timestamp
        |FROM Violations v
        |JOIN Vehicles ve ON v.vehicle_id = ve.vehicle_id
        |JOIN TrafficSignals t ON v.signal_id = t.signal_id
        |ORDER BY v.timestamp DESC
        |""".stripMargin

    val stmt = conn.prepareStatement(query)
    val rs = stmt.executeQuery()
    println("\n🚨 Violations:")
    while (rs.next()) {
      println(s"${rs.getInt("violation_id")}: ${rs.getString("license_plate")} @ ${rs.getString("location")} - ${rs.getString("violation_type")} [${rs.getTimestamp("timestamp")}]")
    }
    rs.close(); stmt.close()
  }
}
