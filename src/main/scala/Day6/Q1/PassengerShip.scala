package Day6.Q1

class PassengerShip(val fuelLevelCapacity:Double) extends SpaceCraft {
  override def launch(): Unit = {
    println(s"Passengership launching with fuel level $fuelLevelCapacity%")
    println("All passengers secured....")
  }

  override def land(): Unit = {
    println("PassengerShip landing: landing smoothly....")
  }

}
