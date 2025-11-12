package Day5.Group8

object SafeDivision extends App{

  def safeDivide(a: Int, b: Int): Either[String, Double]={
      if(b==0)Left("Division by Zero")
      else
       Right(a/b)
  }


    val pairs = List((10, 2), (5, 0), (8, 4))

  pairs.foreach{ case (a,b) => println(safeDivide(a,b))}

  val res: List[Either[String, Double]] =pairs.map{ case (a,b)=> safeDivide(a,b)}

  val (errors, validResults) = res.partitionMap(identity)

  println(validResults)

}
