import org.apache.spark.{SparkConf, SparkContext}

object UrbanMoveTripsFilter {
  def main(args: Array[String]): Unit = {
    // Create Spark configuration
    val conf = new SparkConf()
      .setAppName("UrbanMoveTripsFilter")
      .setMaster("local[*]") // Use "yarn" for cluster mode

    // Create SparkContext
    val sc = new SparkContext(conf)

    try {
      // Load the CSV file as RDD
      val tripsRDD = sc.textFile("urbanmove_trips.csv")

      // Get the header
      val header = tripsRDD.first()
      println(header)

      // Remove header and process data
      val dataRDD = tripsRDD.filter(row => row != header && row.nonEmpty)

      // Parse CSV rows and handle possible issues
      val parsedRDD = dataRDD.map { row =>
        // Using split with limit -1 to preserve empty fields
        row.split(",", -1).map(_.trim)
      }

      // Filter trips with distance > 10 km and map to (vehicleType, distance)
      // Index mapping based on your header:
      // 0: tripId, 1: driverId, 2: vehicleType, 7: distanceKm
      val filteredMappedRDD = parsedRDD
        .filter { cols =>
          // Ensure we have enough columns and distance column exists
          cols.length > 7 && cols(7).nonEmpty
        }
        .filter { cols =>
          try {
            // Parse distance and check if > 10 km
            cols(7).toDouble > 10.0
          } catch {
            case _: NumberFormatException => false
          }
        }
        .map { cols =>
          // Map to (vehicleType, distance)
          (cols(2), cols(7).toDouble)
        }

      // Optional: Show some results for verification
      println("=== Sample Results ===")
      filteredMappedRDD.take(5).foreach { case (vehicleType, distance) =>
        println(s"$vehicleType: $distance km")
      }

      // Save output as text file
      // This will create a directory with multiple part files
      filteredMappedRDD.saveAsTextFile("output/vehicle_type_distance")

      // If you want a single output file, you can coalesce:
      // filteredMappedRDD.coalesce(1).saveAsTextFile("output/vehicle_type_distance_single")

      println(s"\nTotal trips with distance > 10 km: ${filteredMappedRDD.count()}")
      println("Output saved to: output/vehicle_type_distance")

    } finally {
      // Stop SparkContext
      sc.stop()
    }
  }
}