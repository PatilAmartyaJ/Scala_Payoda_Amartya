
package repositories

import models.{CheckIn, Room}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import tables.CheckIns

@Singleton
class CheckInRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val checkIns = TableQuery[CheckIns]

  def create(checkIn: CheckIn): Future[CheckIn] = {
    val insert = checkIns += checkIn
    db.run(insert).map(_ => checkIn)  // Return the same room, since ID is already generated
  }

  def findById(id: String): Future[Option[CheckIn]] =
    db.run(checkIns.filter(_.checkInId === id).result.headOption)

}

