package Services


import com.google.protobuf.CodedInputStream
import com.typesafe.config.ConfigFactory
import environmental.Daily_Anomaly_Counts.Daily_Anomaly_Counts
import environmental.Daily_Avg_Co2_By_Zone.{DailyAvgCo2ByZoneProto, Daily_Avg_Co2_By_Zone}
import environmental.Daily_Avg_Pm_By_Zone.Daily_Avg_Pm_By_Zone
import models.DailySummary

import javax.inject._
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.services.s3.model.GetObjectRequest

import java.sql.Date
import java.time.LocalDate
import scala.concurrent.ExecutionContext

@Singleton
class DashboardSummaryService @Inject()( s3connector:S3Connector)(implicit ec: ExecutionContext) {

  val config = ConfigFactory.load()




  val S3_BUCKET_NAME = config.getString("s3.bucket")
  // AWS credentials and region from env
  val S3_ACCESS_KEY = config.getString("s3.access_key")
  val S3_SECRET_KEY = config.getString("s3.secret_key")

  private val region = config.getString("s3.region")
  // Lazy S3 client initialization
  lazy val s3: S3Client = S3Client.builder()
    .region(Region.of(region))
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsBasicCredentials.create(S3_ACCESS_KEY, S3_SECRET_KEY)
      )
    )
    .build()

  private def readBytes(key: String): Array[Byte] = {
    val obj = s3.getObject(
      GetObjectRequest.builder()
        .bucket(S3_BUCKET_NAME)
        .key(key)
        .build()
    )
    obj.readAllBytes()
  }

  def fetchTodaySummary(zoneId: String): DailySummary = {
    val today = LocalDate.now().minusDays(7).toString

    val pm = Daily_Avg_Pm_By_Zone.parseFrom(
      CodedInputStream.newInstance(readBytes(s"pm_summary/event_date=$today/zone=$zoneId.pb"))
    )

    val co2 = Daily_Avg_Co2_By_Zone.parseFrom(
      CodedInputStream.newInstance(readBytes(s"co2_summary/event_date=$today/zone=$zoneId.pb"))
    )

    val anom = Daily_Anomaly_Counts.parseFrom(
      CodedInputStream.newInstance(readBytes(s"anomaly_summary/event_date=$today/batch-$today.pb"))
    )
    DailySummary(
      zone_id = zoneId,
      event_Date = Date.valueOf(LocalDate.now().minusDays(7)),
      avg_pm10 = pm.avgPm10,
      avg_pm25 = pm.avgPm25,
      avg_co2 = co2.avgCo2
    )


  }
}