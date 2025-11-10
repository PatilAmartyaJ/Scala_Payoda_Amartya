package Day2

import java.util.{Date,Calendar}
import scala.util.Random
import java.util.Date
import java.text.SimpleDateFormat



class Payodian(val name:String, val doj:Date, val client: String, val salary: Double) {

  def this(name:String, doj:Date, client:String)= {
    this(name,doj,client,20000)
  }

  def this(name:String, doj:Date)={
    this(name,doj,"To be assigned",20000)
  }
  def welcome(name:String): String = {
     f"Welcome ${name} to Payoda Technologies and you will be working on ${client} project in next 2 months"
  }
  val id: String=Payodian.generateId(12)

   // operator overloading
  def +(increment:Double): Double= {
    // increment salary
    return salary+(salary*increment)/100
  }
  def +(extention:Int): Date = {
    // extend the date of joining
    val cal = Calendar.getInstance()
    cal.setTime(doj)
    cal.add(Calendar.DAY_OF_MONTH, extention)
    cal.getTime

  }

}
object Payodian{
    private var employee_id: String=_


    def generateId(length:Int): String = {
      val charstr:String="ABCDEFGHIGHIJKLMNOPQRSTUVWXYZ"
      val sb=new StringBuilder()
      for(i<-1 to length){
        val index=Random.nextInt(length)
        sb.append(charstr.charAt(index))
      }
      sb.toString()
    }

  

}
