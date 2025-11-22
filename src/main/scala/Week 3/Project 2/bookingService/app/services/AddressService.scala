package services

import jakarta.inject.Inject
import models.{Address, Hotel}
import repositories.{AddressRepository}


import java.sql.Timestamp
import java.time.Instant
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressService @Inject()(addressRepo: AddressRepository)(implicit ec: ExecutionContext) {
  def createAddress(
                     addressLine1: String,
                     addressLine2: Option[String],
                     city: String,
                     state: Option[String],
                     country: String,
                     postalCode: Option[String],
                   ): Future[Address] = {

    val now = Timestamp.from(Instant.now())

    val address = Address.create(
      addressLine1,
      addressLine2,
      city,
      state,
      country,
      postalCode
    )
    addressRepo.create(address)

  }

}

