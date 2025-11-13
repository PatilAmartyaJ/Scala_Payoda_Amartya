package Day6.Q4

trait CommunicationModule extends Drone{
  def sendMessage(msg: String): Unit = println(s"Sending message: $msg")

  // Optional override
   override def deactivate(): Unit = println("Communication module shutting down")
}

