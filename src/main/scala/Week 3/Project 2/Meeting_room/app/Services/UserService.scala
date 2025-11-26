package Services

import Models._
import Repositories.UserRepository

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(
                             userRepo: UserRepository
                           )(implicit ec: ExecutionContext) {

  def getUserById(userId: String): Future[Option[User]] = {
    userRepo.findById(userId)
  }

  def getUserByEmail(email: String): Future[Option[User]] = {
    userRepo.findByEmail(email)
  }

  def validateUserExists(userId: String): Future[Boolean] = {
    userRepo.findById(userId).map(_.isDefined)
  }

  def validateUserActive(userId: String): Future[Boolean] = {
    userRepo.findById(userId).map {
      case Some(user) => user.isActive
      case None => false
    }
  }

  def validateUserCanBookRoom(userId: String): Future[Either[String, User]] = {
    userRepo.findById(userId).map {
      case Some(user) if !user.isActive =>
        Left("User account is deactivated")
      case Some(user) if user.role == UserRole.RoomServiceTeam.toString =>
        Left("Room service team members cannot book rooms")
      case Some(user) =>
        Right(user)
      case None =>
        Left("User not found")
    }
  }

  def validateAdminUser(userId: String): Future[Either[String, User]] = {
    userRepo.findById(userId).map {
      case Some(user) if !user.isActive =>
        Left("User account is deactivated")
      case Some(user) if isAdminUser(user) =>
        Right(user)
      case Some(user) =>
        Left("Insufficient permissions. Admin access required.")
      case None =>
        Left("User not found")
    }
  }

  private def isAdminUser(user: User): Boolean = {
    user.role == UserRole.SuperAdmin.toString ||
      user.role == UserRole.SystemAdmin.toString ||
      user.role == UserRole.AdminStaff.toString
  }

  def createUser(userRequest: CreateUserRequest): Future[Either[String, User]] = {
    // Check if email already exists
    userRepo.findByEmail(userRequest.email).flatMap {
      case Some(_) =>
        Future.successful(Left("User with this email already exists"))
      case None =>
        // Check if username already exists
        userRepo.findByUsername(userRequest.username).flatMap {
          case Some(_) =>
            Future.successful(Left("Username already taken"))
          case None =>
            // Check if employee ID already exists
            userRepo.findByEmployeeId(userRequest.employeeId).flatMap {
              case Some(_) =>
                Future.successful(Left("Employee ID already exists"))
              case None =>
                val user = User.create(
                  username = userRequest.username,
                  email = userRequest.email,
                  passwordHash = hashPassword(userRequest.password), // You should implement proper password hashing
                  firstName = userRequest.firstName,
                  lastName = userRequest.lastName,
                  department = userRequest.department,
                  employeeId = userRequest.employeeId,
                  phone = userRequest.phone,
                  role = userRequest.role.toString
                )
                userRepo.create(user).map(Right(_))
            }
        }
    }
  }

  def updateUser(userId: String, userUpdate: UpdateUserRequest): Future[Either[String, User]] = {
    userRepo.findById(userId).flatMap {
      case Some(existingUser) =>
        // If email is being updated, check if it's already taken by another user
        val emailCheck = userUpdate.email match {
          case Some(newEmail) if newEmail != existingUser.email =>
            userRepo.findByEmail(newEmail).map {
              case Some(otherUser) if otherUser.user_id != userId => Left("Email already taken by another user")
              case _ => Right(())
            }
          case _ => Future.successful(Right(()))
        }

        // If username is being updated, check if it's already taken by another user
        val usernameCheck = userUpdate.username match {
          case Some(newUsername) if newUsername != existingUser.username =>
            userRepo.findByUsername(newUsername).map {
              case Some(otherUser) if otherUser.user_id != userId => Left("Username already taken by another user")
              case _ => Right(())
            }
          case _ => Future.successful(Right(()))
        }

        for {
          emailResult <- emailCheck
          usernameResult <- usernameCheck
          result <- (emailResult, usernameResult) match {
            case (Right(_), Right(_)) =>
              val updatedUser = existingUser.copy(
                username = userUpdate.username.getOrElse(existingUser.username),
                email = userUpdate.email.getOrElse(existingUser.email),
                firstName = userUpdate.firstName.getOrElse(existingUser.firstName),
                lastName = userUpdate.lastName.getOrElse(existingUser.lastName),
                department = userUpdate.department.getOrElse(existingUser.department),
                phone = userUpdate.phone.orElse(existingUser.phone),
                role = userUpdate.role.map(_.toString).getOrElse(existingUser.role),
                isActive = userUpdate.isActive.getOrElse(existingUser.isActive),
                updatedAt = new Timestamp(System.currentTimeMillis())
              )
              userRepo.update(updatedUser).map {
                case true => Right(updatedUser)
                case false => Left("Failed to update user")
              }
            case (Left(emailError), _) => Future.successful(Left(emailError))
            case (_, Left(usernameError)) => Future.successful(Left(usernameError))
          }
        } yield result

      case None =>
        Future.successful(Left("User not found"))
    }
  }

  def deactivateUser(userId: String, deactivatedBy: String): Future[Either[String, Boolean]] = {
    for {
      adminValidation <- validateAdminUser(deactivatedBy)
      result <- adminValidation match {
        case Right(_) =>
          userRepo.deactivate(userId).map {
            case true => Right(true)
            case false => Left("User not found or already deactivated")
          }
        case Left(error) => Future.successful(Left(error))
      }
    } yield result
  }

  def getUsersByRole(role: UserRole): Future[Seq[User]] = {
    userRepo.findByRole(role)
  }

//  def searchUsers(searchCriteria: UserSearchCriteria): Future[Seq[User]] = {
//    userRepo.searchUsers(searchCriteria)
//  }

  def getAllActiveUsers(): Future[Seq[User]] = {
    userRepo.findAllActive()
  }

  private def hashPassword(password: String): String = {
    // Implement proper password hashing (e.g., using BCrypt)
    // This is a placeholder - you should use a proper hashing library
    java.security.MessageDigest.getInstance("SHA-256")
      .digest(password.getBytes("UTF-8"))
      .map("%02x".format(_)).mkString
  }
}

// Request/Response case classes
case class CreateUserRequest(
                              username: String,
                              email: String,
                              password: String,
                              firstName: String,
                              lastName: String,
                              department: String,
                              employeeId: String,
                              phone: Option[String] = None,
                              role: UserRole
                            )

case class UpdateUserRequest(
                              username: Option[String] = None,
                              email: Option[String] = None,
                              firstName: Option[String] = None,
                              lastName: Option[String] = None,
                              department: Option[String] = None,
                              phone: Option[String] = None,
                              role: Option[UserRole] = None,
                              isActive: Option[Boolean] = None
                            )

case class UserSearchCriteria(
                               department: Option[String] = None,
                               role: Option[UserRole] = None,
                               isActive: Option[Boolean] = Some(true),
                               searchTerm: Option[String] = None
                             )
