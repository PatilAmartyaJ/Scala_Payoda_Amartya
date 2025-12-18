package Services

import com.datastax.oss.driver.api.core.CqlSession
import com.typesafe.config.ConfigFactory
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

import java.net.URI
import javax.inject._

@Singleton
class S3Connector @Inject() (){
  val config = ConfigFactory.load()
  private val bucketName = config.getString("s3.bucket")
  private val accessKey = config.getString("s3.access_key")
  private val secretKey = config.getString("s3.secret_key")
  private val region = config.getString("s3.region")
  private val endpoint = config.getString("s3.endpoint")
  private val enablePathStyle = config.getString("s3.path.style.access")

  // Create AWS credentials
  private val credentials = AwsBasicCredentials.create(accessKey, secretKey)
  private val credentialsProvider = StaticCredentialsProvider.create(credentials)

  // Build S3 client
  private val s3Client: S3Client = {
    val builder = S3Client.builder()
      .region(Region.of(region))
      .credentialsProvider(credentialsProvider)

    // If using a custom endpoint (like MinIO or other S3-compatible services)
    if (endpoint != "s3.amazonaws.com") {
      builder.endpointOverride(URI.create(s"https://$endpoint"))
    }

    // Enable path-style access if needed
    if (enablePathStyle.toBoolean) {
      builder.serviceConfiguration(
        software.amazon.awssdk.services.s3.S3Configuration.builder()
          .pathStyleAccessEnabled(true)
          .build()
      )
    }

    builder.build()
  }
  def getSession: S3Client = s3Client


}
