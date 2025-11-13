package Day6.Q4

object DroneFleetSystem extends App {
  val drone1 = new BasicDrone with NavigationModule with DefenseModule
  val drone2 = new BasicDrone with DefenseModule with NavigationModule
  val drone3 = new BasicDrone with NavigationModule with DefenseModule with CommunicationModule

  println("=== Drone1 ===")
  drone1.activate()
  drone1.flyTo("Sector 7")
  drone1.activateShields()
  drone1.status()
  drone1.deactivate()

  println("\n=== Drone2 ===")
  drone2.deactivate()

  println("\n=== Drone3 ===")
  drone3.flyTo("Alpha Base")
  drone3.activateShields()
  drone3.sendMessage("Mission start")
  drone3.deactivate()


}
