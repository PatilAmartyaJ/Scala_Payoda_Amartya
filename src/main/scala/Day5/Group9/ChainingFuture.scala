package Day5.Group9
object ChainingFuture extends App {

  import scala.concurrent._
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.util._

  def getUser(id: Int): Future[String] = Future {
    println(s"Fetching user $id")
    s"User$id"
  }

  def getOrders(user: String): Future[List[String]] = Future {
    println(s"Getting orders for $user")
    List(s"$user-order1", s"$user-order2")
  }

  def getOrderTotal(order: String): Future[Double] = Future {
    val amount = scala.util.Random.between(10.0, 100.0)
    println(s"Total for $order = $amount")
    amount
  }

  val totalPipeline: Future[Double] = for {
    user <- getUser(42)
    orders <- getOrders(user)
    totals <- Future.sequence(orders.map(getOrderTotal))
  } yield totals.sum

  val totalAmount = Await.result(totalPipeline, 5.seconds)
  println(s"Total amount for all orders: $totalAmount")
}
