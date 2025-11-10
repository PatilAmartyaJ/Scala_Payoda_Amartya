package Day5.Group1

import scala.annotation.tailrec

object MaxArrayElem extends App{

  val arr=Array(1,23,4,45,5)

  println(maxArrayElem(arr))

  def maxArrayElem(arr:Array[Int]):Int={
    @tailrec
    def helper(index:Int, currentMax:Int):Int={
      if(index<0)currentMax
      else{
        val newMax=if(arr(index)>currentMax) arr(index) else currentMax
        helper(index-1,newMax)
      }


    }
    if(arr.isEmpty)throw new NoSuchElementException("Array is Empty")
    else helper(arr.length-1,arr(arr.length-1))
  }
}
