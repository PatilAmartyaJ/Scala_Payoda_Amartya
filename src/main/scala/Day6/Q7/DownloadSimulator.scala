package Day6.Q7

object DownloadSimulator {

  // Define a DownloadTask class that extends Thread
  class DownloadTask(fileName: String, downloadSpeed: Int) extends Thread {
    override def run(): Unit = {
      for (progress <- 10 to 100 by 10) {
        try {
          Thread.sleep(downloadSpeed) // simulate time taken per 10% chunk
        } catch {
          case e: InterruptedException =>
            println(s"$fileName download interrupted!")
        }
        println(s"$fileName: $progress% downloaded")
      }
      println(s"$fileName download completed! ✅")
    }
  }

  def main(args: Array[String]): Unit = {
    println("📥 File Download Progress Simulator Started\n")

    // Create multiple DownloadTask threads with different speeds (ms per 10%)
    val fileA = new DownloadTask("FileA.zip", 800)
    val fileB = new DownloadTask("FileB.mp4", 500)
    val fileC = new DownloadTask("FileC.pdf", 300)

    // Start all downloads concurrently
    fileA.start()
    fileB.start()
    fileC.start()

    // Wait for all threads to finish
    fileA.join()
    fileB.join()
    fileC.join()

    println("\n✅ All downloads completed successfully!")
  }
}
