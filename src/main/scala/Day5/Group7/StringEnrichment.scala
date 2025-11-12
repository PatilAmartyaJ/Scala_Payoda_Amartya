package Day5.Group7



object StringEnrichment extends App{
    val str1="Hello"
    val str2="World"

    println("hi!"*3)
    println("Hello"~"world")


  implicit class RichString(val str: String) {
    def *(times: Int): String = {
      var sb = new StringBuilder()
      for (i <- 1 to times) {
        sb.append(str)
      }
      sb.toString()
    }


    def ~(other: String): String = {
      var sb = new StringBuilder()
      sb.append(str)
      sb.append(" ")
      sb.append(other)
      sb.toString()
    }

  }

}

