package Util


object IDGenerator {

  private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
  private val random = new scala.util.Random()

  sealed trait IdType { def length: Int; def prefix: String }
  object IdType {
    case object Zone     extends IdType { val length = 12; val prefix = "ZN" }
    case object Sensor     extends IdType { val length = 8;  val prefix = "SENS" }
    case object PollutionThreshold     extends IdType { val length = 7;  val prefix = "PTH"  }

    def fromString(str: String): IdType = str match {
      case "Zone"     => Zone
      case "Sensor"     => Sensor
      case "PollutionThreshold" => PollutionThreshold
      case other       => throw new IllegalArgumentException(s"Unknown ID type: $other")
    }

  }

  def generate(idType: IdType): String = {
    val rand = (1 to idType.length)
      .map(_ => chars(random.nextInt(chars.length)))
      .mkString

    s"${idType.prefix}-$rand"
  }


}


