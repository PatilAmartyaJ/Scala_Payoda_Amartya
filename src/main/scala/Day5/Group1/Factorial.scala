package Day5.Group1

import scala.annotation.tailrec

object Factorial extends App {


  println(factorial(5))
  def factorial(n: Int):Int={
    @tailrec
    def fact_helper(x:Int, accumulator: Int): Int={
       if(x==0)accumulator

       else
         println("Acc= "+accumulator+" n= "+x)
         fact_helper(x-1,x*accumulator)
    }
    fact_helper(n,1)
  }
}
