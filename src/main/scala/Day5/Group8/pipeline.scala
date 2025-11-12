package Day5.Group8

import scala.util.{Try,Success}

object pipeline extends App{
  val data = List("10", "20", "x", "30")
  val parsed = data.map(s => Try(s.toInt))

  val validInts = parsed.collect { case Success(n) => n }

  val squared = validInts.map(n => n * n)

 
  val result = data
    .map(s => Try(s.toInt)) 
    .collect { case Success(n) => n } 
    .map(n => n * n) 

  println(result) 

}
