package Day3


  object scalaDay3Examples extends App {

    // Lazy
    lazy val lazyVal = {
      println("Lazy evaluated"); 100
    }

    // Tail recursion
    @annotation.tailrec
    def factorial(n: Int, acc: Long = 1): Long =
      if (n == 0) acc else factorial(n - 1, acc * n)

    // Function returning function
    def power(exp: Int): Int => Int = x => Math.pow(x, exp).toInt

    // Keyword params
    def welcome(name: String, title: String = "Mr.") =
      s"Welcome, $title $name"

    // Varargs
    def avg(nums: Int*): Double = nums.sum.toDouble / nums.size

    // Higher-order function
    def operate(f: Int => Int, x: Int) = f(x)

    // Collections
    val data = List(1, 2, 3, 4)

    // Partial application
    def add(a: Int, b: Int) = a + b

    val add5 = add(5, _: Int)

    // Currying
    def curriedAdd(a: Int)(b: Int) = a + b

    // Generics
    def headOption[T](list: List[T]): Option[T] = list.headOption

    println(lazyVal)
    println(factorial(5))
    println(power(3)(2))
    println(welcome("Smith"))
    println(avg(1, 2, 3, 4))
    println(operate(_ + 10, 5))
    println(data.map(_ * 2))
    println(add5(10))
    println(curriedAdd(5)(7))
    println(headOption(List("A", "B")))
  }

