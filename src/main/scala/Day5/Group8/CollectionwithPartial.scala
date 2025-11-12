package Day5.Group8

object CollectionwithPartial extends App {
  val items = List(1, "apple", 3.5, "banana", 42)
    
  

  val collectInts: PartialFunction[Any, Int] = {
    case i: Int => Math.abs(i)
  }


  val abs=items.collect(collectInts)
  print(abs)

}
