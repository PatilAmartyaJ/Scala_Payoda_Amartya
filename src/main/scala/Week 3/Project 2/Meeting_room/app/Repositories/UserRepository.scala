package Repositories

import Models.{User, UserRole}
import Tables.UserTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository@Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  val users = TableQuery[UserTable]

  def findById(id: String): Future[Option[User]] = {
    db.run(users.filter(_.user_id === id).result.headOption)
  }

  def findByRole(role: UserRole): Future[Seq[User]] = {
    db.run(users.filter(u => u.role === role.toString && u.isActive === true).result)
  }

  def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(u => u.email === email).result.headOption)
  }

  def findByUsername(username: String): Future[Option[User]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }

  def findByEmployeeId(employeeId: String): Future[Option[User]] = {
    db.run(users.filter(_.employeeId === employeeId).result.headOption)
  }

  def create(user: User): Future[User] = {
    val insert = users += user
    db.run(insert).map(_ => user)

  }

  def update(user: User): Future[Boolean] = {
    db.run(users.filter(_.user_id === user.user_id).update(user)).map(_ > 0)
  }

  def deactivate(userId: String): Future[Boolean] = {
    db.run(
      users.filter(_.user_id === userId)
        .map(u => (u.isActive, u.updatedAt))
        .update((false, new Timestamp(System.currentTimeMillis())))
    ).map(_ > 0)
  }


  def findAllActive(): Future[Seq[User]] = {
    db.run(users.filter(_.isActive === true).result)
  }

  def findAll(): Future[Seq[User]] = {
    db.run(users.result)
  }

}
