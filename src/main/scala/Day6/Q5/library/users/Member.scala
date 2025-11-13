package Day6.Q5.library.users


import Day6.Q5.library.items.ItemType

class Member(val name: String) {
  def borrowItem(item: ItemType): Unit = {
    println(s"$name borrowed '${item.title}'")
  }
}

