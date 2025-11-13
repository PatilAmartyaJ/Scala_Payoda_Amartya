package Day6.Q4

trait DefenseModule extends Drone{
  def activateShields(): Unit = println("Shields activated")

  // Optional override
  override def deactivate(): Unit = println("Defense systems deactivated")
}

