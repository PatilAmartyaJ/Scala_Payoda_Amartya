package Day6.Q1

abstract class SpaceCraft {
  val fuelLevelCapacity:Double;

  def launch():Unit
  def land(): Unit={
    println("Spacecraft is landing....")
  }

}


