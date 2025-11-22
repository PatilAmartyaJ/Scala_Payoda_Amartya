package services

import models.{Guest, Hotel}

import javax.inject.{Inject, Singleton}
import repositories.{GuestRepository, HotelRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GuestService @Inject()(guestRepo: GuestRepository)(implicit ec: ExecutionContext) {

  def createGuest(
                   first_name: String,
                   last_name: String,
                   email: String,
                   phone: String,
                   gender: String,
                   Id_type: String,
                   Id_issue_country: String,
                   address_Id: String,
                   nationalities: Seq[String],
                 ): Future[Guest] = {


    val guest = Guest.create(first_name,
      last_name,
      email,
      phone,
      gender,
      Id_type,
      Id_issue_country,
      address_Id,
      nationalities: Seq[String])

    guestRepo.create(guest)
  }


}
