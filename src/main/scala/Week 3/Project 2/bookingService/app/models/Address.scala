package models

import play.api.libs.json.{Format, JsError, JsNumber, JsResult, JsSuccess, JsValue, Json}
import util.IDGenerator.IdType
import util.IDGenerator

import java.sql.Timestamp

case class Address(
                    addressId: String,
                    addressLine1: String,
                    addressLine2: Option[String],
                    city: String,
                    state: Option[String],
                    country: String,
                    postalCode: Option[String],
                    createdAt: Timestamp,
                    updatedAt: Timestamp
                  )

object Address {

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsNumber(ts.getTime)
    def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsNumber(n) => JsSuccess(new Timestamp(n.toLong))
      case _ => JsError("Expected number for timestamp")
    }
  }

  implicit val format: Format[Address] = Json.format[Address]

  def create(addressLine1: String,
             addressLine2: Option[String],
             city: String,
             state: Option[String],
             country: String,
             postalCode: Option[String],
            ): Address = {
    val now = new Timestamp(System.currentTimeMillis())
    new Address(
      addressId = IDGenerator.generate(IdType.Address),
      addressLine1 = addressLine1,
      addressLine2 = addressLine2,
      city = city,
      state = state,
      country = country,
      postalCode = postalCode,
      createdAt = now,
      updatedAt = now
    )

  }
}
//ADR-OQ2DRnf7