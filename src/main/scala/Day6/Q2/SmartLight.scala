package Day6.Q2

class SmartLight extends Device with Connectivity with EnergySaver {

  override def turnOn(): Unit = {
    println("Smart light is started with adjustable brightness!")
  }

  
}
