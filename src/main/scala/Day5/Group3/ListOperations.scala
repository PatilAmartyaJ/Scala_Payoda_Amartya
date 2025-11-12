package Day5.Group3

object ListOperations extends App{

  //The Mystery of :+ and +:

  val nums = List(2, 4, 6)
  val newList = nums :+ 8
  println(newList)

  val newList2 = 0 +: newList
  println(newList2)


  println(Counter(5)+Counter(7))
  println(Counter(5)+10)

  val list = 1 :: 2 :: 3 :: 4:: Nil
  println(list)

  val a=List(1,2)
  val b=List(3,4)
  val c1=a++b
  val c2=a ::: b

  println(c1)
  println(c2)


  val animals = Map(
    "dog" -> "bark",
    "cat" -> "meow",
    "cow" -> "moo"
  )
  val animals2=animals+("lion"->"roar")
  println(animals2.get("cow"))
  println(animals2.get("tiger")) // it will print None because map.get() returns null , it will not give you  null pointer exception like in java

  println(animals2.getOrElse("Tiger","roar"))




}
