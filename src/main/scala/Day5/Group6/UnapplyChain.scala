package Day5.Group6

object UnapplyChain extends App{

  case class Address(city: String, pincode: Int)

  case class Person(name: String, address: Address)


  val p = Person("Ravi", Address("Mumbai", 600001))
  p match {
    case Person(_, Address(city, pin)) 
      if city.startsWith("C") => println(s"$city - $pin")
    case _ => println("No match")
  }

}
