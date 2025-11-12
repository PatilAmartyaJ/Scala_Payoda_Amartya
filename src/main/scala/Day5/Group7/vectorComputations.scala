package Day5.Group7

object vectorComputations extends App {

 val v1=Vec2D(2,3)
 val v2=Vec2D(4,1)

 println((v1+v2).toString)
 println((v1-v2).toString)
 println((v1*3).toString)
 println((3*v1).toString)

}
class Vec2D(val x: Int, val y: Int) {

  def +( v1: Vec2D): Vec2D = new Vec2D(this.x + v1.x, this.y + v1.y)

  def -(v1: Vec2D): Vec2D = new Vec2D(this.x - v1.x, this.y - v1.y)

  def *(scalar:Int):  Vec2D=new Vec2D(this.x*scalar,this.y*scalar)

  override def toString: String = s"($x, $y)"


}
object Vec2D {
  def apply(x: Int, y: Int): Vec2D = new Vec2D(x, y)


  implicit class IntOperation(val scalar: Int) extends AnyVal {
    def *(v: Vec2D): Vec2D = new Vec2D(v.x * scalar, v.y * scalar)
  }
}


