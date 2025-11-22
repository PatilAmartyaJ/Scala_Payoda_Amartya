package repositories

import models.{Address, Hotel, Room}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.Addresses

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val addresses = TableQuery[Addresses]

  def create(address: Address): Future[Address] = {
    db.run(addresses += address).map(_ => address)

  }

  def findById(addressId: String): Future[Option[Address]] =
    db.run(addresses.filter(_.addressId === addressId).result.headOption)
}


