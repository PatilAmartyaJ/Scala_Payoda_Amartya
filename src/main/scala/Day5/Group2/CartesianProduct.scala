package Day5.Group2

object CartesianProduct extends App{

  val students:List[String]=List("Asha", "Bala", "Chitra")
  val subjects:List[String]=List("Math","Physics")

  print(cartesianProductwithFilter(students,subjects))

    def cartesianProductwithFilter(students:List[String],subjects:List[String]):List[(String, String)]={
      val ans = students.flatMap { student =>
        subjects.filter(subject => student.length > subject.length)
          .map(subject => (student, subject))
      }
      ans
    }

}
