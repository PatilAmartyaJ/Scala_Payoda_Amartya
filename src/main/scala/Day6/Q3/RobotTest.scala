package Day6.Q3

object RobotTest extends App {

  // Robot with speech and movement capabilities
  val robot1 = new BasicRobot with SpeechModule with MovementModule
  robot1.start()             // BasicRobot starting up
  robot1.status()            // Robot is operational
  robot1.speak("Hello!")     // Robot says: Hello!
  robot1.moveForward()       // Moving forward
  robot1.shutdown()          // BasicRobot shutting down

  println("-----")

  // Robot with energy saver and movement capabilities
  val robot2 = new BasicRobot with EnergySaver with MovementModule
  robot2.start()             // BasicRobot starting up
  robot2.moveBackward()      // Moving backward
  robot2.activateEnergySaver() // Energy saver mode activated
  robot2.shutdown()          //shutdown Robot shutting down to save energy (from EnergySaver)
}
