package Day5.Group9

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._
import scala.concurrent.Await

object RiskyOperation extends App{

  def riskyOperation(): Future[Int] = Future {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Failed!")
    n
  }

  val result1: Future[Int] = riskyOperation().recover {
    case ex: Throwable =>
      println(s"Recovering from error: ${ex.getMessage}")
      -1 // fallback value
  }

  

  val value1 = Await.result(result1, 5.seconds)
  println(s"Result (with recover): $value1")


}
