package Day6.Q3

// Base class
class BasicRobot extends Robot {
  override def start(): Unit = println("BasicRobot starting up")
  override def shutdown(): Unit = println("BasicRobot shutting down")
}
