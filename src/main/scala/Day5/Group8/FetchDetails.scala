package Day5.Group8

object FetchDetails extends App{
  def fetchData(): Int = {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Network fail")
    n
  }


  val data = retry(3)(fetchData())
  println(s"Final result: $data")


  def retry[T](times: Int)(op: => T): Option[T] = {
    var attempts = 0
    var result: Option[T] = None

    while (attempts < times && result.isEmpty) {
      try {
        result = Some(op) // try the operation
      } catch {
        case e: Throwable =>
          println(s"Attempt ${attempts + 1} failed: ${e.getMessage}")
      }
      attempts += 1
    }

    result
  }

}
