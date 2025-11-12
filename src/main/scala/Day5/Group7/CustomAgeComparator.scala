package Day5.Group7

object CustomAgeComparator extends App {
   val p1=Person("Ravi",23)
   val p2=Person("Meena",30)
   println(p1<p2)
   println(p1>p2)
   println(p1>=p2)


}
case class Person(val name: String, val age: Int)
object Person {


  implicit class PersonOps(p: Person) {
    def >(other: Person): Boolean = p.age > other.age

    def >=(other: Person): Boolean = p.age >= other.age

    def <(other: Person): Boolean = p.age < other.age

    def <=(other: Person): Boolean = p.age <= other.age
  }
}

