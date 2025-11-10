package Day2
import java.text.SimpleDateFormat
import java.util.Date

object Day2 extends App{
  val format = new SimpleDateFormat("yyyy-MM-dd")


  val payodian1=new Payodian("Amatya",format.parse("2025-11-03"),"Apple",500000)
  println(payodian1.id)



  print(payodian1+10)




}
