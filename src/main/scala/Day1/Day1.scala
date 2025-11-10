package Day1
import scala.io.StdIn
import scala.io.StdIn.readLine

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import scala.collection.mutable

object Day1 extends App{

  // There are two ways of declaring variables in the scala
  // var => mutable variables
  // val=> immutable variables
  var maths_score: Int=70
  // You can explicitly declare the type of the variable, scala support type inference so if
  // we don't mention the type scala compiler will automatically detect type of variable.
  maths_score=71

  val total_score=100
  // here you cannot reassign or change the value assigned to total score.
  // If we are trying to reassign it will throw error *Reassignment to val*

  //Data Types, Scala Supports all the dara types provided in the java
  //Sr No | Datatype | Description (Range) | Default values
  // 1| Byte | 8-bit numbers are represented -128 to 127 => 0
  // 2| Short | 16-bit numbers are represented -32768 to 32767 => 0
  // 3| Int | 32-bit numbers are represented -2147483648 to 2147483647 => 0
  // 4| Long | 64-bit numbers are represented -9223372036854775808 to 9223372036854775807 => 0L
  // 5| Float | 32-bit decimal numbers are represented => 0.0F
  // 6| Double | 64-bit decimal numbers are represented => 0.0D
  // 7| Char | 16 bit unsigned unicode characters are represented. =>'\u000'
  // 8| String | A sequence of characters => null
  // 9| Boolean | true or false value of literal are represented => False
  // 10| Unit | similar to void datatype in java, corresponds to no value=> No default value
  // 11| Null | Null or empty reference => No default value
  // 12| Nothing | subtype of every type => No default value
  // 13| Any | supertype of any type => No default value
  // 14| AnyRef | supertype of any reference type. => No default value

  // If you want to initialize these variables with default values  you must use var for Initialization. *val* this type of initialization will throw an error

  var num:Int=_
  println(num)// It will print 0, if it was boolean variable answer could be false and so on....

  //val name:String=_ //Unbound placeholder parameter; incorrect use of _

  var amIGood: Boolean = true
  var amIbyte: Byte = 126
  var pi: Float = 3.14f
  var totalCupCakes: Int = 40
  var a4: Short = 45
  var a5: Double = 2.93846523
  var a6: Char = 'A'
  var string_plain: String="Amartya"
  var height: Double=1.7523
  var string_multiline: String="""
    Sanmanniya Vyaspith
    Vyaspithavar jamalele
    manyavar aani mazya
    Bandhu Bhaginino
    """
  print(string_multiline)
    var string_interpolated: String = s"Hi ${string_plain}"

//    | Specifier | Type | Meaning | Example |
//    | --------- | -------------- | --------------------- | ------------------------------- |
//    | %d | Integer | Decimal integer | f"$x%d" → 42 |
//    | %f | Float / Double | Floating -point number | f"$pi%1.2f" → 3.14 |
//    | %s | String | String value | f"$name%s" → "Alice" |
//    | %c | Char | Single character | f"$ch%c" → 'A' |
//    | %b | Boolean | true / false | f"$flag%b" |
//    | %x | Integer | Hexadecimal | f"$num%x" → "2a" |
//    | %e | Float / Double | Scientific notation | f"$pi%e" → "3.141593e+00" |
//    | %o | Integer | Octal representation | f"$num%o" → "52" |
//    | %t | Date / Time | Date / time formatting | f"$date%tY" → "2025" (year) |
//    %[width].[precision]f, %f for floating number
//    width (2) → The minimum total number of characters to display (including digits, decimal point, and spaces for padding).
//    If the number is longer, it just expands — it’s not truncated.
//    precision (.2) → Number of digits after the decimal point.
  var string_formated: String=f"Hi myself ${string_plain}%s and I am ${height}%2.2fm tall"
  println(string_formated)




  // decision statements
  // Here I am taking input from user and whether he or she is eligible for voting or not we will decide it.
  // functions, case statement, regex covered
//  print("Enter your Name: ")
//  val name= readLine()
//  println("Enter your Birthdate: ")
//  println("Please write it in (DD-MM-YYYY) format: ")
//  val dateFormat="dd-MM-yyyy"
//  val formatter = DateTimeFormatter.ofPattern(dateFormat)
//
//  val date=readLine()
//  if(validateDate(date)){
//    val curr_date: LocalDate = LocalDate.now()
//    val birthdate= LocalDate.parse(date,formatter)
//    val age=Period.between(birthdate,curr_date)
//    println(age.getYears)
//    if(age.getYears>=18){
//      println(s"${name}, you are eligible for voting!")
//    }
//    else{
//      println(s"${name}, you are not eligible for voting")
//    }
//  }
//  else{
//    println("Please enter the date in proper format ")
//  }





  def validateDate(date: String) : Boolean = {
    val datePattern = """(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\d{4})""".r

    date match {
      case datePattern(date, month, year) => {

        val extracted_date = date.toInt
        val extracted_month = month.toInt
        val extracted_year = year.toInt
        val month30 = List(4, 6, 9, 11)

        if (extracted_month == 2) {
          if (isYearLeap(extracted_year)) extracted_date <= 29 else extracted_date <= 28

        } else if (month30.contains(extracted_month)) {
          extracted_date <= 30
        }
        else {
          extracted_date <= 31
        }

      }
      case _ => return false
    }
  }

  def isYearLeap(year: Int): Boolean = {
    if(year<1900)return false;// assuming people having birthdate before 1900 died till 2025
    if(year%100==0)return year%400==0
    return year%4==0
  }





  // functions
  //   (basic function concepts, overloading, function with default parameters
  //   ,Units)

   //  Immutable collections | `scala.collection.immutable` | ❌ Cannot change | `List` , `Vector` , `Set` , `Map` |
  //    Mutable collections  | `scala.collection.mutable` | ✅ Can change | `ArrayBuffer`, `ListBuffer`, `HashMap` |

   val months30 = List(4, 6, 9, 11)// This is one of the ways to you can initialize the lists

   // In scala Immutable lists are internally Linked Lists so inserting new element at the end will take O(N) time complexity


   println(months30.head) // 4
   println(months30.tail) // List(6, 9, 11)


  val nums = 1 :: 2 :: 3 :: 4 :: 5::Nil
  println(nums) // List(1, 2, 3, 4, 5)



  println(nums.length) // 5
  println(nums.isEmpty) // false
  println(nums.reverse) // List(5, 4, 3, 2, 1)
  println(nums.contains(3)) // true

  println(nums.sum) // 15
  println(nums.max) // 5
  println(nums.min) // 1


  val newlist = List(10, 20, 30, 40, 11, 22, 33, 44 ,55)

  val doubled = newlist.map(_ * 2)
  println(doubled) // List(20, 40, 60, 80, 22, 44, 66, 88, 110)

  val evens = newlist.filter(_ % 2 == 0)
  println(evens) // List(10, 20, 30, 40, 22, 44)

  //since lists are immutable

  val list1 = List('a', 'b','c')
  val list2 = List('d', 'e')
  val combined = list1 ++ list2
  val newList = 'z' +: list1 :+ 'f'
  println(newList) // List('z','a','b','c','f')

  println(combined) // List('a','b','c','d','e')


  val fruits = List("apple", "banana", "orange", "grape", "pomegranate", "mango", "Custard Apple", "Chickoo")

  for (fruit <- fruits) {
    println(fruit)
  }

  // or using foreach
  fruits.foreach(println)

  val capitals = Map("Maharashtra" -> "Mumbai", "Karnataka" -> "Bengaluru", "Goa" -> "Panaji", "Telangana" -> "Hyderabad", "Kerala" -> "Thiruvanatapuram")

  // Try to add a new entry
  val updated = capitals + ("Madhyapradesh" -> "Bhopal")

  println(capitals) // Map(France -> Paris, Japan -> Tokyo)
  println(updated) // Map(France -> Paris, Japan -> Tokyo, India -> Delhi)




  val capitals2 = mutable.Map("Maharashtra" -> "Mumbai", "Karnataka" -> "Bengaluru", "Goa" -> "Panaji", "Telangana" -> "Hyderabad", "Kerala" -> "Thiruvanatapuram")
  capitals2 += ("Andhrapradesh" -> "Hyderabad") // adds entry
  capitals2 -= "Goa" // removes entry

  println(capitals) // Map(France -> Paris, India -> Delhi)


  def area(radius: Double): Double = 3.14 * radius * radius

  def area(length: Double, breadth: Double): Double = length * breadth

  println(area(5)) // Circle area
  println(area(4, 6)) // Rectangle area


}
