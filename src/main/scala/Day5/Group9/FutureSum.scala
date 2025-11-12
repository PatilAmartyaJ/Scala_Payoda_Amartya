package Day5.Group9
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

object FutureSum extends App {

  val f1 = Future { Thread.sleep(1000); 10 }
  val f2 = Future { Thread.sleep(800); 20 }
  val f3 = Future { Thread.sleep(500); 30 }

  val result: Future[String] = Future.sequence(List(f1, f2, f3)).map { values =>
    val sum = values.sum
    val avg = sum / values.length
    s"Sum = $sum, Average = $avg"
  }

  result.onComplete {
    case Success(msg) => println(msg)
    case Failure(ex) => println(s"Failed: ${ex.getMessage}")
  }

  // Keep JVM alive until futures complete
  Thread.sleep(1500)
}

