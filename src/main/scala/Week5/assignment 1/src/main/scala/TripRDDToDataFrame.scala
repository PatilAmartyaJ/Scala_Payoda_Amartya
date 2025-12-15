import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.rdd.RDD
import scala.util.Try

object TripRDDToDataFrame {

  // Define your case class
  case class Trip(
                   tripId: Long,
                   driverId: Int,
                   vehicleType: String,
                   startTime: String,
                   endTime: String,
                   startLocation: String,
                   endLocation: String,
                   distanceKm: Double,
                   fareAmount: Double,
                   paymentMethod: String,
                   customerRating: Double
                 )

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("TripRDDToDataFrame")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    try {
      println("=== Loading and converting RDD to DataFrame ===")

      // 1. Load the CSV file as RDD[String]
      val rawRDD: RDD[String] = spark.sparkContext.textFile("urbanmove_trips.csv")

      // 2. Get header and data
      val header = rawRDD.first()
      val dataRDD = rawRDD.filter(row => row != header && row.nonEmpty)

      // 3. Parse CSV rows and create Trip objects
      val tripRDD: RDD[Trip] = dataRDD.flatMap { row =>
        val cols = row.split(",", -1).map(_.trim)

        // Ensure we have enough columns (at least 11)
        if (cols.length >= 11) {
          Try {
            Trip(
              tripId = cols(0).toLong,
              driverId = cols(1).toInt,
              vehicleType = cols(2),
              startTime = cols(3),
              endTime = cols(4),
              startLocation = cols(5),
              endLocation = cols(6),
              distanceKm = cols(7).toDouble,
              fareAmount = cols(8).toDouble,
              paymentMethod = cols(9),
              customerRating = cols(10).toDouble
            )
          }.toOption
        } else {
          None // Skip rows that don't have enough columns
        }
      }

      println(s"Successfully parsed ${tripRDD.count()} trips")

      // 4. Convert RDD[Trip] to DataFrame using createDataFrame()
      // Method 1: Using spark.createDataFrame() directly
      val tripDF: DataFrame = spark.createDataFrame(tripRDD)

      // Method 2: Using toDF() with implicits (alternative)
      // val tripDF = tripRDD.toDF()

      // 5. Show schema and sample data
      println("\n=== DataFrame Schema ===")
      tripDF.printSchema()

      println("\n=== Sample Data (first 5 rows) ===")
      tripDF.show(5, truncate = false)

      println("\n=== Saving DataFrame ===")

      // Save as Parquet (columnar format, efficient for analytics)
      tripDF.write.parquet("output/trips_parquet")
      println("Saved as Parquet: output/trips_parquet")

      // Save as CSV
      tripDF.write
        .option("header", "true")
        .csv("output/trips_csv")
      println("Saved as CSV: output/trips_csv")

      // Save as JSON
      tripDF.write.json("output/trips_json")
      println("Saved as JSON: output/trips_json")

    } catch {
      case e: Exception =>
        println(s"Error occurred: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}
