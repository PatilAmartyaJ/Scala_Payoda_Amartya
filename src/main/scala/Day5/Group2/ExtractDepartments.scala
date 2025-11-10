package Day5.Group2

object ExtractDepartments extends App {
  val departments = List(
    ("IT", List("Ravi", "Meena")),
    ("HR", List("Anita")),
    ("Finance", List("Vijay", "Kiran"))
  )

  extractDepartments(departments).foreach(println)

  def extractDepartments(deptList: List[(String, List[String])]): List[String] = {
    val extracted = for {
      (dept, employees) <- deptList
      emp <- employees
    } yield s"$dept: $emp"
    extracted.toList
  }

}
