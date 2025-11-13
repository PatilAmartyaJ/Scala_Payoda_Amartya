package Day6.Q5
import Day6.Q5.library.items._
import Day6.Q5.library.users._
import Day6.Q5.library.operations.LibraryOps._


object LibraryMain extends App{
  // Explicit member
  val alice = new Member("Alice")
  val book1: Book = Book("Scala Programming")
  
  borrow(book1)(alice) // Alice borrows 'Scala Programming'

  // Using implicit default member
  val dvd1: DVD = DVD("Inception")
  borrow(dvd1) // Default Member borrows 'Inception'

  // Using implicit conversion from String to Book
  borrow("Harry Potter") // Default Member borrows 'Harry Potter'

  // Demonstrate sealed trait pattern matching
  val items: List[ItemType] = List(Book("FP in Scala"), Magazine("Science Today"), DVD("Matrix"))
  items.foreach(itemDescription)
  

}
