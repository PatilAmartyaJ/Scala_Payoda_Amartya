package Day5.Group7

import scala.language.implicitConversions

object ChainedImplicit extends App{

  case class Rational(n:Int,d:Int)

  val r1=Rational(3,2)

  val r2= Rational(2,3)


  println((r1/r2).toString)
  println((1/r1).toString)


  implicit class RationalDivision(val num:Int) extends AnyVal{
    def /(another: Rational): Rational = {
      Rational(num * another.d, another.n)
    }
  }

  


  implicit class RationalConversion(val num:Rational) extends AnyVal{
    def /(another:Rational):Rational={
      Rational(num.n*another.d,num.d*another.n)
    }


  }

}
