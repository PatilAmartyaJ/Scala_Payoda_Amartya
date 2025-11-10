package Day5.Group1

object TemperatureConverter extends App{
  println(convertTemp(0,"C"))
  println(convertTemp(212,"F"))
  println(convertTemp(50,"X"))


  def convertTemp(value: Double, scale: String): Double = {
    if (scale == "C") {
      celciusToFahrenheit(value)

    } else if (scale == "F") {
      fahrenheitToCelcius(value)
    }
    else {
      value
    }
  }

  def celciusToFahrenheit(temp_celcius: Double): Double = {
    (temp_celcius * 9 / 5) + 32
  }

  def fahrenheitToCelcius(temp_fahrenheit: Double): Double = {
    (temp_fahrenheit - 32) * 5 / 9
  }

}
