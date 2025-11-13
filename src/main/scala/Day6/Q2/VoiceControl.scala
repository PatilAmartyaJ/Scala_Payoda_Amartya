package Day6.Q2

trait VoiceControl extends SmartLight {
  
  override def turnOn(): Unit = println("Voice command: Turn on device")

  override def turnOff(): Unit = println("Voice command: Turn off device")
}

