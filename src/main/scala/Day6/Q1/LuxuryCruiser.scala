package Day6.Q1

import Day6.Q1.Autopilot

final class LuxuryCruiser(override val fuelLevelCapacity: Double)
  extends PassengerShip(fuelLevelCapacity) with Autopilot {

  def playEntertainment(): Unit = {
    println("Playing onboard entertainment: Galaxy Symphony in 4D surround sound.")
  }

  override def autoNavigate(): Unit = {
    println("LuxuryCruiser autopilot activated: navigating with premium comfort mode.")
  }
}
