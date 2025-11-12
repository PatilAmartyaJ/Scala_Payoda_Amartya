package Day5.Group4

object MoodModifier extends App {

  def moodChanger(pref: String): String => String = {
    midd => s"$pref-$midd-$pref"
  }

  val happyMood = moodChanger("happy")
  println(happyMood("day")) // "happy-day-happy"

  val angryMood = moodChanger("angry")
  println(angryMood("crowd")) // "angry-crowd-angry"


  val trimSpaces: String => String = _.trim
  val toLower: String => String = _.toLowerCase
  val capitalizeFirst: String => String = s => s.head.toUpper.toString + s.tail

  val normalize: String => String = trimSpaces andThen toLower andThen capitalizeFirst

  println(normalize("  Amartya patil ")) // Amartya patil


  delayedMessage(5000, 1000)("Amartya")

  def delayedMessage(upto: Int, delayMs: Int)(message: String): Unit = {
    var value = upto
    while (value > 0) {
      Thread.sleep(delayMs)
      println(message)
      value -= 1000
    }


  }

  def discountStrategy(memberType: String): Double => Double={
    memberType match {
     case "gold" => price => price * 0.80
     case "silver" => price => price * 0.90
     case _ => price => price
   }
  }

  val goldDiscount = discountStrategy("gold")
  println(goldDiscount(1000)) // 800.0

  val safeDivide: PartialFunction[Int, String] = {
    case x if x != 0 => "Result: " + (100 / x)
  }

  val safe = safeDivide.lift

  println(safeDivide.isDefinedAt(10))
  println(safeDivide.isDefinedAt(0))

  println(safe(10)) // Some("Result: 10")
  println(safe(0)) // None


}
