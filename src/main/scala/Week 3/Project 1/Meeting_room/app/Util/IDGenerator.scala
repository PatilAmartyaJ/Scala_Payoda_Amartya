package Util


object IDGenerator {

  private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
  private val random = new scala.util.Random()

  sealed trait IdType { def length: Int; def prefix: String }
  object IdType {
    case object User     extends IdType { val length = 12; val prefix = "GST" }
    case object Notification     extends IdType { val length = 6;  val prefix = "NTN" }
    case object Room      extends IdType { val length = 4;  val prefix = "RM"  }
    case object Booking  extends IdType { val length = 5;  val prefix = "BK" }
    def fromString(str: String): IdType = str match {
      case "User"     => User
      case "Notification"     => Notification
      case "Room"      => Room
      case "Booking"  => Booking
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

