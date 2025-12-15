import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import java.sql.Timestamp
import scala.util.Try

object CleanTripDataFrame {

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
    // Create SparkSession
    val spark = SparkSession.builder()
      .appName("CleanTripDataFrame")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    try {
      println("=== Loading and Cleaning Trip DataFrame ===")

      // 1. Load data and convert to DataFrame
      val tripDF = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv("urbanmove_trips.csv")
        .as[Trip]  // Convert to Dataset[Trip]

      val initialCount = tripDF.count()
      println(s"Initial record count: $initialCount")

      // 2. Show initial stats
      println("\n=== Initial Summary Statistics ===")
      tripDF.describe("distanceKm", "fareAmount", "customerRating").show()

      // 3. Clean the DataFrame step by step
      var cleanedDF = tripDF

      // Step 1: Remove rows where distanceKm <= 0
      println("\n=== Step 1: Removing trips with distance <= 0 ===")
      val beforeDistanceFilter = cleanedDF.count()
      cleanedDF = cleanedDF.filter($"distanceKm" > 0.0)
      val afterDistanceFilter = cleanedDF.count()
      val removedDistance = beforeDistanceFilter - afterDistanceFilter
      println(s"Removed $removedDistance trips with distance <= 0")

      // Step 2: Remove trips where fareAmount < 0
      println("\n=== Step 2: Removing trips with fareAmount < 0 ===")
      val beforeFareFilter = cleanedDF.count()
      cleanedDF = cleanedDF.filter($"fareAmount" >= 0.0)
      val afterFareFilter = cleanedDF.count()
      val removedFare = beforeFareFilter - afterFareFilter
      println(s"Removed $removedFare trips with negative fare")

      // Step 3: Validate and remove trips where startTime >= endTime
      println("\n=== Step 3: Validating startTime < endTime ===")

      // Define a function to parse timestamps
      def parseTimestamp(timeStr: String): Option[Timestamp] = {
        Try {
          // Try different date formats
          if (timeStr.contains("T")) {
            // ISO format: "2023-01-01T10:30:00"
            Timestamp.valueOf(timeStr.replace("T", " "))
          } else if (timeStr.contains("/")) {
            // US format: "01/01/2023 10:30:00"
            val parts = timeStr.split(" ")
            if (parts.length == 2) {
              val dateParts = parts(0).split("/")
              if (dateParts.length == 3) {
                // Convert mm/dd/yyyy to yyyy-mm-dd
                val isoDate = s"${dateParts(2)}-${dateParts(0)}-${dateParts(1)}"
                Timestamp.valueOf(s"$isoDate ${parts(1)}")
              } else null
            } else null
          } else {
            // Assume it's already in SQL format
            Timestamp.valueOf(timeStr)
          }
        }.toOption
      }

      // Register UDF for timestamp parsing
      val parseTimestampUDF = udf((timeStr: String) =>
        parseTimestamp(timeStr).orNull
      )

      // Add parsed timestamp columns
      val dfWithTimestamps = cleanedDF
        .withColumn("parsedStartTime", parseTimestampUDF($"startTime"))
        .withColumn("parsedEndTime", parseTimestampUDF($"endTime"))

      // Filter valid trips (both times parseable and start < end)
      val beforeTimeFilter = dfWithTimestamps.count()

      val validTripsDF = dfWithTimestamps
        .filter($"parsedStartTime".isNotNull && $"parsedEndTime".isNotNull)
        .filter($"parsedStartTime" < $"parsedEndTime")

      val afterTimeFilter = validTripsDF.count()
      val removedTimeInvalid = beforeTimeFilter - afterTimeFilter

      println(s"Removed $removedTimeInvalid trips with invalid or reversed timestamps")

      // Get the invalid time records for analysis
      val invalidTimeRecords = dfWithTimestamps
        .filter($"parsedStartTime".isNull || $"parsedEndTime".isNull || $"parsedStartTime" >= $"parsedEndTime")
        .select($"tripId", $"startTime", $"endTime", $"parsedStartTime", $"parsedEndTime")

      println("\n=== Sample of Invalid Time Records ===")
      if (invalidTimeRecords.count() > 0) {
        invalidTimeRecords.show(5, truncate = false)
      }

      // 4. Final cleaned DataFrame (drop temporary timestamp columns)
      val finalDF = validTripsDF
        .drop("parsedStartTime", "parsedEndTime")
        .cache()  // Cache for multiple operations

      // 5. Show cleaning summary
      println("\n=== Cleaning Summary ===")
      println(s"Initial records: $initialCount")
      println(s"After distance filter (> 0 km): $afterDistanceFilter")
      println(s"After fare filter (>= 0): $afterFareFilter")
      println(s"After time validation (start < end): $afterTimeFilter")
      println(s"Total records removed: ${initialCount - afterTimeFilter}")
      println(s"Cleaning efficiency: ${((initialCount - afterTimeFilter).toDouble / initialCount * 100).formatted("%.2f")}% records removed")

      // 6. Show cleaned data statistics
      println("\n=== Cleaned Data Statistics ===")
      finalDF.describe("distanceKm", "fareAmount", "customerRating").show()

      println("\n=== Sample Cleaned Data ===")
      finalDF.show(10, truncate = false)


      // pipeline 4
      val df2 = finalDF
        .withColumn("parsedStartTime",
          to_timestamp(col("startTime"), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        .withColumn("parsedEndTime",
          to_timestamp(col("endTime"), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        .withColumn("tripDurationSeconds",
          unix_timestamp(col("parsedEndTime")) - unix_timestamp(col("parsedStartTime")))
        .withColumn("tripDurationMinutes",
          col("tripDurationSeconds") / 60.0)
        .drop("parsedStartTime", "parsedEndTime", "tripDurationSeconds")


      df2.show(5,truncate = false)



      // pipeline 5

      val df3= finalDF.withColumn("startDate", to_date(to_timestamp($"startTime", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")))
        .withColumn("route", concat($"startLocation", lit(" -> "), $"endLocation"))

      val avgDistanceByVehicle = df3
        .groupBy("vehicleType")
        .agg(
          avg("distanceKm").alias("avg_distance_km"),
        )
        .orderBy("vehicleType")

      avgDistanceByVehicle.show(4,truncate= false)


      val revenuePerDay = df3
        .groupBy("startDate")
        .agg(
          sum("fareAmount").alias("daily_revenue")
        )
        .orderBy("startDate")
      revenuePerDay.show(5,truncate = false)


      val routeAnalysis = df3
        .groupBy("startLocation", "endLocation", "route")
        .agg(
          count("*").alias("trip_count"),
        )
        .orderBy(desc("trip_count"))
      routeAnalysis.show(5,truncate=false)


     finalDF.show(5,false)

      finalDF.createOrReplaceTempView("trips")
      //pipeline 6
      println("\n=== 1. AVERAGE TRIP DISTANCE BY VEHICLE TYPE ===")
      spark.sql("""
      SELECT vehicleType,
             COUNT(*) as trip_count,
             ROUND(AVG(distanceKm), 2) as avg_distance_km
      FROM trips
      GROUP BY vehicleType
      ORDER BY avg_distance_km DESC
    """).show()

      println("\n=== 2. REVENUE PER DAY ===")
      spark.sql("""
      SELECT DATE(startTime) as trip_date,
             COUNT(*) as trips,
             ROUND(SUM(fareAmount), 2) as daily_revenue
      FROM trips
      GROUP BY DATE(startTime)
      ORDER BY trip_date
    """).show(10)

      println("\n=== 3. TOP 5 MOST USED ROUTES ===")
      spark.sql("""
      SELECT startLocation, endLocation,
             COUNT(*) as route_count
      FROM trips
      GROUP BY startLocation, endLocation
      ORDER BY route_count DESC
      LIMIT 5
    """).show()
      // Save as Parquet (recommended for analytics)
      finalDF.write
        .mode("overwrite")
        .parquet("output/cleaned_trips_parquet")
      println("Saved cleaned data as Parquet: output/cleaned_trips_parquet")

      // Save as CSV with proper header
      finalDF.write
        .mode("overwrite")
        .option("header", "true")
        .csv("output/cleaned_trips_csv")
      println("Saved cleaned data as CSV: output/cleaned_trips_csv")



    } catch {
      case e: Exception =>
        println(s"Error occurred: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\nSparkSession stopped.")
    }
  }


  def executeSQLAnalytics(spark: SparkSession): Unit = {
    import spark.sql

    try {
      // 1. Average trip distance by vehicleType
      println("\n1. AVERAGE TRIP DISTANCE BY VEHICLE TYPE")
      val query1 = """
        SELECT
          vehicleType,
          COUNT(*) as total_trips,
          ROUND(AVG(distanceKm), 2) as avg_distance_km,
          ROUND(MIN(distanceKm), 2) as min_distance_km,
          ROUND(MAX(distanceKm), 2) as max_distance_km
        FROM trips
        GROUP BY vehicleType
        ORDER BY avg_distance_km DESC
      """
      println(s"Executing query:\n$query1")
      sql(query1).show()

      // 2. Revenue per day
      println("\n2. REVENUE PER DAY")
      val query2 = """
        SELECT
          tripDate,
          COUNT(*) as trips_count,
          ROUND(SUM(fareAmount), 2) as total_revenue,
          ROUND(AVG(fareAmount), 2) as avg_fare_amount
        FROM trips
        GROUP BY tripDate
        ORDER BY tripDate
      """
      sql(query2).show(10)

      // 3. Top 5 most used routes
      println("\n3. TOP 5 MOST USED ROUTES")
      val query3 = """
        SELECT
          startLocation,
          endLocation,
          COUNT(*) as trip_count,
          ROUND(AVG(distanceKm), 2) as avg_distance,
          ROUND(AVG(fareAmount), 2) as avg_fare,
          ROUND(SUM(fareAmount), 2) as total_revenue
        FROM trips
        GROUP BY startLocation, endLocation
        ORDER BY trip_count DESC
        LIMIT 5
      """
      sql(query3).show()

    } catch {
      case e: Exception =>
        println(s"SQL Query Error: ${e.getMessage}")
        throw e
    }
  }
}