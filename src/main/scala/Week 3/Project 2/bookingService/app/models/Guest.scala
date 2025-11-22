package models

import util.IDGenerator.IdType
import util.IDGenerator

import java.sql.Timestamp

case class Guest(
                  guest_id: String,
                  first_name: String,
                  last_name: String,
                  full_name: String,
                  email: String,
                  phone: String,
                  gender: String,
                  Id_type: String,
                  Id_issue_country: String,
                  address_Id: String,
                  nationalities: Seq[String],
                  created_at: Timestamp,
                  updated_at: Timestamp
                )

object Guest {
  def create(
              first_name: String,
              last_name: String,
              email: String,
              phone: String,
              gender: String,
              Id_type: String,
              Id_issue_country: String,
              address_Id: String,
              nationalities: Seq[String],

            ): Guest = {
    val fullName = s"$first_name $last_name"
    val now = new Timestamp(System.currentTimeMillis())
    new Guest(
      guest_id = IDGenerator.generate(IdType.Guest),
      first_name=first_name,
      last_name=last_name,
      full_name=fullName,
      email=email,
      phone=phone,
      gender=gender,
      Id_type=Id_type,
      Id_issue_country=Id_issue_country,
      address_Id=address_Id,
      nationalities=nationalities,
      created_at=now,
      updated_at=now
    )
  }
}