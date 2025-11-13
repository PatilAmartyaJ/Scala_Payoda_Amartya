package Day6.Q3

// Trait for energy-saving behavior
trait EnergySaver extends Robot {
  def activateEnergySaver(): Unit = println("Energy saver mode activated")
  override def shutdown(): Unit = println("Robot shutting down to save energy") // overrides Robot.shutdown
}
