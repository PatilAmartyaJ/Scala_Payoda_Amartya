package Day5.Group6

object BlockEvaluation extends App{
  //A by-name parameter means “don’t evaluate this expression yet — pass the code itself.”
  //The function (or apply method) decides when to run it.

  object Evaluator {
    def apply(block: => Any): Unit = {
      println(s"Evaluating block...")
      val result = block
      println(s"Result = $result")
    }
  }

  Evaluator {
    val x = 5
    val y = 3
    x * y + 2
  }


}
