package Day6.Q2

trait EnergySaver {


  def activateEnergySaver(): Unit = {
    println("Energy saver mode activated")
  }

  def turnOff(): Unit = {
    println("Device powered down to save energy") //(overrides Device.turnOff())
  }

  


}
