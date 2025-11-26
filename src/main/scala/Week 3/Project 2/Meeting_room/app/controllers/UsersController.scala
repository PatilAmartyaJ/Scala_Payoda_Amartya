// app/controllers/UsersController.scala
package controllers

import Models._
import Services.{CreateUserRequest, UpdateUserRequest, UserService}

import javax.inject._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import Util.JsonFormats._

@Singleton
class UsersController @Inject()(
                                 cc: ControllerComponents,
                                 userService: UserService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc)  {

  private val logger = play.api.Logger(this.getClass)

  // Explicit JSON formats for request/response case classes
  implicit val createUserRequestFormat: Format[CreateUserRequest] = Json.format[CreateUserRequest]
  implicit val updateUserRequestFormat: Format[UpdateUserRequest] = Json.format[UpdateUserRequest]
  //implicit val userSearchCriteriaFormat: Format[UserSearchCriteria] = Json.format[UserSearchCriteria]

  def createUser = Action.async(parse.json) { implicit request =>
    logger.info("Received create user request")

    request.body.validate[CreateUserRequest] match {
      case JsSuccess(userRequest, _) =>
        userService.createUser(userRequest).map {
          case Right(user) =>
            logger.info(s"User created successfully: ${user.username}")
            Created(Json.toJson(user))
          case Left(error) =>
            logger.warn(s"Failed to create user: $error")
            Conflict(Json.obj("error" -> error))
        }.recover {
          case ex: Exception =>
            logger.error("Error creating user", ex)
            InternalServerError(Json.obj("error" -> "Internal server error"))
        }

      case JsError(errors) =>
        logger.error(s"Invalid create user request: $errors")
        Future.successful(BadRequest(Json.obj("error" -> "Invalid request body", "details" -> JsError.toJson(errors))))
    }
  }

  def getUser(userId: String) = Action.async { implicit request =>
    userService.getUserById(userId).map {
        case Some(user) => Ok(Json.toJson(user))
        case None       => NotFound(Json.obj("error" -> "User not found"))
      }

  }

  def getUserByEmail(email: String) = Action.async { implicit request =>
    userService.getUserByEmail(email).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("error" -> "User not found"))
    }
  }

  def updateUser(userId: String) = Action.async(parse.json) { implicit request =>
    request.body.validate[UpdateUserRequest] match {
      case JsSuccess(userUpdate, _) =>
        userService.updateUser(userId, userUpdate).map {
          case Right(user) =>
            logger.info(s"User updated successfully: $userId")
            Ok(Json.toJson(user))
          case Left(error) =>
            logger.warn(s"Failed to update user $userId: $error")
            BadRequest(Json.obj("error" -> error))
        }.recover {
          case ex: Exception =>
            logger.error(s"Error updating user: $userId", ex)
            InternalServerError(Json.obj("error" -> "Internal server error"))
        }

      case JsError(errors) =>
        logger.error(s"Invalid update user request: $errors")
        Future.successful(
          BadRequest(Json.obj(
            "error" -> "Invalid request body",
            "details" -> JsError.toJson(errors)
          ))
        )
    }
  }

  def deactivateUser(userId: String) = Action.async(parse.json) { implicit request =>
    val deactivatedBy: String =
      (request.body \ "deactivatedBy").asOpt[String].getOrElse("UNKNOWN")

    userService.deactivateUser(userId, deactivatedBy).map {
      case Right(_) =>
        logger.info(s"User deactivated successfully: $userId")
        Ok(Json.obj("message" -> "User deactivated successfully"))
      case Left(error) =>
        logger.warn(s"Failed to deactivate user $userId: $error")
        BadRequest(Json.obj("error" -> error))
    }.recover {
      case ex: Exception =>
        logger.error(s"Error deactivating user: $userId", ex)
        InternalServerError(Json.obj("error" -> "Internal server error"))
    }
  }

  def getUsersByRole(role: String) = Action.async { implicit request =>
    parseUserRole(role) match {
      case Some(userRole) =>
        userService.getUsersByRole(userRole).map { users =>
          Ok(Json.toJson(users))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("error" -> "Invalid role")))
    }
  }

//  def searchUsers = Action.async(parse.json) { implicit request =>
//    request.body.validate[UserSearchCriteria] match {
//      case JsSuccess(criteria, _) =>
//        userService.searchUsers(criteria).map { users =>
//          Ok(Json.toJson(users))
//        }
//      case JsError(errors) =>
//        logger.error(s"Invalid search criteria: $errors")
//        Future.successful(BadRequest(Json.obj("error" -> "Invalid search criteria", "details" -> JsError.toJson(errors))))
//    }
//  }

  def getAllActiveUsers = Action.async { implicit request =>
    userService.getAllActiveUsers().map { users =>
      Ok(Json.toJson(users))
    }
  }

  def validateUser(userId: String) = Action.async { implicit request =>
    userService.validateUserCanBookRoom(userId).map {
      case Right(user) =>
        Ok(Json.obj(
          "valid" -> true,
          "user" -> Json.obj(
            "id" -> user.user_id,
            "name" -> s"${user.firstName} ${user.lastName}",
            "role" -> user.role
          )
        ))

      case Left(error) =>
        Ok(Json.obj(
          "valid" -> false,
          "error" -> error
        ))
    }
  }

  // Helper methods

  private def trySome[A](block: => A): Option[A] = {
    try Some(block) catch { case _: Exception => None }
  }

  private def parseUserRole(role: String): Option[UserRole] = {
    role.toUpperCase match {
      case "SUPERADMIN" => Some(UserRole.SuperAdmin)
      case "SYSTEMADMIN" => Some(UserRole.SystemAdmin)
      case "ADMINSTAFF" => Some(UserRole.AdminStaff)
      case "EMPLOYEE" => Some(UserRole.Employee)
      case "ROOMSERVICETEAM" => Some(UserRole.RoomServiceTeam)
      case _ => None
    }
  }
}