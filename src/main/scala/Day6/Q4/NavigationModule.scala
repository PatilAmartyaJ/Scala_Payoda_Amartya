package Day6.Q4

trait NavigationModule extends Drone {
  def flyTo(destination: String): Unit = println(s"Flying to $destination")

  // Optional override
  override def deactivate(): Unit = println("Navigation systems shutting down")
}
