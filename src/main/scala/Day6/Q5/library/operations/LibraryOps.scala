package Day6.Q5.library.operations


import Day6.Q5.library.items.*
import Day6.Q5.library.users.*


// Borrow an item using an implicit Member
object LibraryOps {

  // Borrow method
  def borrow(item: ItemType)(implicit member: Member): Unit = {
    member.borrowItem(item)
  }

  // Describe items using pattern matching
  def itemDescription(item: ItemType): String = item match {
    case Book(title) => s"Book: $title"
    case Magazine(title) => s"Magazine: $title"
    case DVD(title) => s"DVD: $title"
  }

  import scala.language.implicitConversions

  // Implicit conversion from String to Book
  implicit def stringToBook(title: String): Book = Book(title)

  // Implicit default member
  implicit val defaultMember: Member = new Member("Default Member")
}

