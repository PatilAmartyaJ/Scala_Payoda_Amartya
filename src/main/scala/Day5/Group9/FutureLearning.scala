package Day5.Group9
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._



object FutureLearning extends App {

  def task(name: String, delay: Int): Future[String] = Future {
    Thread.sleep(delay)
    s"$name done"
  }

  // Sequential
  val startSequential = System.currentTimeMillis()
  println(startSequential)
  val sequential = task("Task1", 1000).flatMap { r1 =>
    println(r1)
    task("Task2", 2000).flatMap { r2 =>
      println(r2)
      task("Task3", 1500).map { r3 =>
        println(r3)
        val end = System.currentTimeMillis()
        println(s"Sequential total time: ${end - startSequential} ms")
      }
    }
  }
  Await.result(sequential, Duration.Inf)
  println("End time sequential "+System.currentTimeMillis())
  // Parallel
  val startParallel = System.currentTimeMillis()
  val parallelTasks = List(
    task("Task1", 1000),
    task("Task2", 2000),
    task("Task3", 1500)
  )

  val parallel = Future.sequence(parallelTasks).map { results =>
    results.foreach(println)
    val end = System.currentTimeMillis()
    println(s"Parallel total time: ${end - startParallel} ms")
  }

 // Await.result(parallel, Duration.Inf)
}
