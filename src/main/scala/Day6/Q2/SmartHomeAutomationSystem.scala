package Day6.Q2

object SmartHomeAutomationSystem extends App{
  val light = new SmartLight
  light.turnOn() // SmartLight turned on
  light.turnOff() // Device powered down to save energy (from EnergySaver)
  light.status() // Device is operational
  light.connect() // Device connected to network
  light.activateEnergySaver() // Energy saver mode activated

  println("-----")

  val thermostat = new SmartThermoStat
  thermostat.turnOn() // SmartThermostat turned on
  thermostat.turnOff() // SmartThermostat turned off
  thermostat.status() // Device is operational
  thermostat.connect() // Device connected to network

  println("-----")

  // Optional: Mixing VoiceControl into SmartLight
  val voiceLight = new SmartLight with VoiceControl
  voiceLight.turnOn() // Voice command: Turn on device
  voiceLight.turnOff() // Voice command: Turn off device

}
