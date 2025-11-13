package Day6.Q5.library.items

// Sealed trait restricts hierarchy to known item types

sealed trait ItemType {
  def title: String
}

// Subclasses
case class Book(title: String) extends ItemType
case class Magazine(title: String) extends ItemType
case class DVD(title: String) extends ItemType