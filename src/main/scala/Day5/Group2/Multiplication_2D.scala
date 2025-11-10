package Day5.Group2

import scala.collection.mutable.ListBuffer

object Multiplication_2D extends App{
   val table: List[String]=multiplication_2D(3)
   table.foreach(println)

  def multiplication_2D(n:Int):List[String]={
    val table = for {
      i <- 1 to n
      j <- 1 to n
    } yield s"$i x $j = ${i * j}" // string interpolation
    table.toList

  }

}
