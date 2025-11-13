package Day6.Q2

trait Device {
  def turnOn(): Unit

  def turnOff(): Unit

  def status(): Unit = {
    println("Device is operational")
  }


}
