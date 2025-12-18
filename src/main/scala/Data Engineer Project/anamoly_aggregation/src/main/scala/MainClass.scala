object MainClass {

  def main(args: Array[String]): Unit = {
    val args=Array("Pipeline5","2025-12-09")
    if(args(0).equals("Pipeline4")){
      AnamolyAggregation.calculateAggregations(args)
    }
    if(args(0).equals("Pipeline5")){
      ProtoBuftable.dumpintos3(args)
    }
  }
}