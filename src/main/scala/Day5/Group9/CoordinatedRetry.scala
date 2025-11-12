package Day5.Group9

object CoordinatedRetry extends App{

  import scala.concurrent._
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.util._

  def fetchDataFromServer(server: String): Future[String] = Future {
    if (Random.nextBoolean()) { // 50% chance to fail
      throw new RuntimeException(s"$server failed!")
    } else {
      s"Data from $server"
    }
  }


  def fetchWithRetry(server: String, maxRetries: Int): Future[String] = {
    fetchDataFromServer(server).recoverWith {
      case ex if maxRetries > 0 =>
        println(s"${ex.getMessage} — retrying, attempts left: ${maxRetries - 1}")
        fetchWithRetry(server, maxRetries - 1) // recurse
    }
  }

  fetchWithRetry("Server-1", 3).onComplete {
    case Success(data) => println(s"Success: $data")
    case Failure(ex) => println(s"Failed after retries: ${ex.getMessage}")
  }

  // Keep JVM alive to allow async operations to complete
  Thread.sleep(5000)

}
