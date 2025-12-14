package controllers

import com.typesafe.config.ConfigFactory
import play.api.mvc._
import play.api.libs.json._
import services.S3Connector
import software.amazon.awssdk.services.s3.model._

import java.nio.file.Files
import javax.inject._

@Singleton
class FileUploadController @Inject()(
                                      cc: ControllerComponents,
                                      s3connector: S3Connector,

                                    ) extends AbstractController(cc) {
  val config = ConfigFactory.load()
  val session = s3connector.getSession

  private val bucket = config.getString("s3.bucket")

  def upload: Action[MultipartFormData[play.api.libs.Files.TemporaryFile]] =
    Action(parse.multipartFormData) { request =>

      request.body.file("file") match {
        case Some(filePart) =>
          val file = filePart.ref.path
          val key =
            s"uploads/${System.currentTimeMillis()}-${filePart.filename}"

          val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(filePart.contentType.orNull)
            .build()

          session.putObject(
            putRequest,
            software.amazon.awssdk.core.sync.RequestBody.fromFile(file)
          )

          Ok(Json.obj(
            "bucket" -> bucket,
            "key" -> key,
            "url" -> s"https://$bucket.s3.amazonaws.com/$key"
          ))

        case None =>
          BadRequest(Json.obj("error" -> "File missing"))
      }
    }
}

