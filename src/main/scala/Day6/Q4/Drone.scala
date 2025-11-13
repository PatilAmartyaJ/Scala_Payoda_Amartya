package Day6.Q4

trait Drone {
  // Abstract methods
  def activate(): Unit

  def deactivate(): Unit

  // Concrete method
  def status(): Unit = println("Drone status: operational")

}
