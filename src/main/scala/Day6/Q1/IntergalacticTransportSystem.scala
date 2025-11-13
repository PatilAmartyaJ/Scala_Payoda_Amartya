package Day6.Q1

import Day6.Q1.CargoShip

object IntergalacticTransportSystem extends App{
  val cargo = new CargoShip(85.0)
  cargo.launch()
  cargo.land()

  val passenger = new PassengerShip(90.0)
  passenger.launch()
  passenger.land()

  val luxury = new LuxuryCruiser(95.0)
  luxury.launch()
  luxury.autoNavigate()
  luxury.playEntertainment()
  luxury.land()
}
