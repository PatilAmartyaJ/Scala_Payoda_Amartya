package Day6.Q3
// Trait for speech capabilities
trait SpeechModule {
  def speak(message: String): Unit = println(s"Robot says: $message")
}

