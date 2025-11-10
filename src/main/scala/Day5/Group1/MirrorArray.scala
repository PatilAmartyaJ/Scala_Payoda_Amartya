package Day5.Group1

object MirrorArray extends App{
  val arr=Array(1,2)
  val mirror_arr=mirrorArray(arr)
  println(mirror_arr.mkString(", "))

  def mirrorArray(arr:Array[Int]): Array[Int]={
    val len=arr.length
    val len_mirror=arr.length*2
    val arr_mirror: Array[Int]=new Array(len_mirror)
    for(index<- 0 until len){
      arr_mirror(index)=arr(index)
    }

    for(index<- len until len_mirror){
      arr_mirror(index)=arr(len_mirror-index-1)
    }

    arr_mirror
  }

}
