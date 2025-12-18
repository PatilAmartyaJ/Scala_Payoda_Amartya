package models

case class DailyPollutionSummary(
                           zone_id: String,
                           name: String,
                           city: String,
                           latitude: Double,
                           longitude: Double,
                           event_Date: java.sql.Date,  // or LocalDate if using Java 8+ time API
                           avg_pm25: Double,
                           avg_pm10: Double,
                           avg_co2: Double,
                           anomaly_count: Long
                         )
