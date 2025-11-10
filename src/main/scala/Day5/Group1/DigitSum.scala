package Day5.Group1

object DigitSum extends App{

  println("Please type numbers in the range -2147483648 to 2147483647 ")
  println(digitSum(1234567891))
   def digitSum(n:Int):Int={
     if(n==0)return 0;
     n%10+digitSum(n/10)
   }

}
