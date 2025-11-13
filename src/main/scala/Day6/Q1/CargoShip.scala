package Day6.Q1

class CargoShip(val fuelLevelCapacity:Double) extends SpaceCraft {
  override def launch(): Unit = {
    println(s"Cargoship launching with fuel level $fuelLevelCapacity%")
    println("Boosting the cargoship....")
  }

  override def land(): Unit = {
    println("Cargoship landing is started, we are landing soon!")
  }


}
