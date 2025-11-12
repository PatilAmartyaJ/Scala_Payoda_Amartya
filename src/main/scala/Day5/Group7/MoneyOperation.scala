package Day5.Group7

import Day5.Group7.MoneyUtils.RichDouble

object MoneyOperation extends App {


  val m1=Money(10.23)
  val m2= Money(5.19)

  println((m1+m2).toString)
  println((m1-m2).toString)



}

class Money(val mon: Double) {
  def +(that:Money): Money={
    println(that.mon+this.mon)
    Money((that.mon+this.mon).roundToNearestPrecision())
  }

  def -(that:Money):Money={
    println(this.mon-that.mon)
    Money((this.mon-that.mon).roundToNearestPrecision())
  }
  override def toString: String = s"($mon)"

}
object MoneyUtils {
  implicit val roundingPrecision: Double = 0.05

  implicit class RichDouble(val number: Double) extends AnyVal {
    def roundToNearestPrecision(precision: Double = roundingPrecision): Double = {
      println(precision)
      require(precision != 0, "Precision cannot be zero")
      Math.round(number / precision) * precision
    }
  }

}
