package Day6.Q2

class SmartThermoStat extends Device with Connectivity {
  override def turnOn(): Unit = println("SmartThermostat turned on")

  override def turnOff(): Unit = println("SmartThermostat turned off")
}

