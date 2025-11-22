package repositories

import models.CheckOut
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.CheckOuts

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckOutRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val checkOuts = TableQuery[CheckOuts]

  def create(checkOut: CheckOut): Future[CheckOut] = {
    val insert = checkOuts += checkOut
    db.run(insert).map(_ => checkOut) // return the same CheckOut
  }

  def findById(id: String): Future[Option[CheckOut]] =
    db.run(checkOuts.filter(_.checkOutId === id).result.headOption)

}
