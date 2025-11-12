package Day5.Group8

object compositionvsThen extends App{

  val trim: String => String = _.trim
  val toInt: String => Int = _.toInt
  val doubleIt: Int => Int = _ * 2
  
  
  val dou=doubleIt.compose(toInt.compose(trim))
  println(dou("21"))
  
  val dou2= trim andThen(toInt andThen( doubleIt))
  println(dou2("    21"))
  
  
}
