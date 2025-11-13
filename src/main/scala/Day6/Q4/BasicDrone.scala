package Day6.Q4

class BasicDrone extends Drone {
  override def activate(): Unit = println("BasicDrone activated")
  override def deactivate(): Unit = println("BasicDrone deactivated")
}

