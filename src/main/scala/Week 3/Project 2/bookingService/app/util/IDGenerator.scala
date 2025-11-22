package util


import scala.collection.mutable



object IDGenerator {

  private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
  private val random = new scala.util.Random()

  sealed trait IdType { def length: Int; def prefix: String }
  object IdType {
    case object Guest     extends IdType { val length = 12; val prefix = "GST" }
    case object Hotel     extends IdType { val length = 6;  val prefix = "HTL" }
    case object Room      extends IdType { val length = 4;  val prefix = "RM"  }
    case object RoomType  extends IdType { val length = 5;  val prefix = "RMT" }
    case object Event     extends IdType { val length = 10; val prefix = "EVT" }
    case object Booking   extends IdType { val length = 9;  val prefix = "BKG" }
    case object Address   extends IdType { val length = 8;  val prefix = "ADR" }
    case object CheckIn   extends IdType { val length = 7;  val prefix=  "CKIN"}
    case object CheckOut  extends IdType {val  length = 11; val prefix=  "CKOUT"}
    def fromString(str: String): IdType = str match {
      case "Guest"     => Guest
      case "Hotel"     => Hotel
      case "Room"      => Room
      case "RoomType"  => RoomType
      case "Event"     => Event
      case "Booking"   => Booking
      case "Address"   => Address
      case "CheckIn"   => CheckIn
      case "CheckOut"  => CheckOut
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


