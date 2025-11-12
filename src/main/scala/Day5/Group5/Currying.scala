package Day5.Group5


object Currying extends App{


  case class Circle(r:Double)
  case class Rectangle(l:Double,b:Double)

  def area(shape:Any):Double= shape match{
    case Circle(r) => Math.round(Math.PI*r*r*100)/100.0
    case Rectangle(l,b)=> l*b
    case _=> -1.0
  }

  println(area(Circle(3))) // 28.27...
  println(area(Rectangle(4, 5))) // 20.0


  def safeDivide(x: Int, y: Int): Option[Int]={
    if (y == 0) None
    else
      Some(x / y)
  }
  println(safeDivide(5,0).getOrElse(-1))

  def calculate(op: String)(x:Int,y:Int):Int = op match {
    case "add"=>x+y
    case "sub"=>x-y
    case "mul"=>x*y
    case "div"=>
      if (y != 0) x / y
      else throw new ArithmeticException("Division by zero")
    case _ => throw new IllegalArgumentException(s"Unknown operator $op")

  }

  val add=calculate("add")_
  val sub=calculate("sub")_
  val multiply= calculate("mul")_
  val div=calculate("div")_

  println(add(10,5))
  println(multiply(3,4))


  def validateLogin(username: String, password: String): Either[String, String]={
    if(username.isEmpty) Left("Username missing")
    else if(password.isEmpty) Left("Password missing")
    else
      Right("Login successful")
  }

  println(validateLogin("","Amartya"))
  println(validateLogin("Amartya","abcdef"))
  println(validateLogin("Amartya",""))


  def parseAndDivide(input: String): Either[String, Int]={
    var int_conversion:Int=0
    try{
      int_conversion=input.toInt


    }
    catch {
      case _:NumberFormatException => return Left("Invalid number")
    }

    val res=safeDivide(100,int_conversion)
    if(res.isDefined)Right(res.get)
    else
      Left("Division by zero")


  }

  println(parseAndDivide("0"))
}
