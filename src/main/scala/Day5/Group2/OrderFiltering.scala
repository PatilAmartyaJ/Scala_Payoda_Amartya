package Day5.Group2

object OrderFiltering extends App{

  case class Order(id: Int, amount: Double, status: String)

  val orders = List(
    Order(1, 1200.0, "Delivered"),
    Order(2, 250.5, "Pending"),
    Order(3, 980.0, "Delivered"),
    Order(4, 75.0, "Cancelled")
  )
  filterOrders("Delivered",500, orders).foreach(println)

  def filterOrders(status:String, minimumOrderAmount: Double, orderlist: List[Order]): List[String] = {
  val filtered=orderlist.filter(order => order.status == status && order.amount > 500)
      .map(order => s"${order.id} -> ${order.amount}")

    filtered
  }

}
