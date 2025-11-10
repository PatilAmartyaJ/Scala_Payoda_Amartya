package Day5.Group2

object WordCount extends App{
  val lines = List(
    "Scala is powerful",
    "Scala is functional and concise",
    "Functional programming is fun"
  )

  print(wordCount(lines))
  def wordCount(lines:List[String]): Map[String,Int] = {
    val wordCount = lines
      .flatMap(line => line.split("\\s+")) // split each line into words
      .groupBy(identity) // group identical words together
      .map { case (word, occurrences) => (word, occurrences.size) } // count

    // identical words with lower and upper case difference spellings will be counted differently
    wordCount

  }



}
