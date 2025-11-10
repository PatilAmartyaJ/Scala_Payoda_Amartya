package Day5.Group3

class Counter(val value:Int) {


  def +(that: Counter): Int = {
    this.value+that.value
  }

  def +(that: Int): Int = {
    this.value+that
  }


}
