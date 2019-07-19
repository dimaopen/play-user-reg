package controllers

import javax.inject._
import models.services.UserService
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @author Dmitry Openkov
  */
@Singleton
class AuthenticationController @Inject()(cc: ControllerComponents, bp: PlayBodyParsers, userService: UserService)
                                        (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def authenticate = Action.async { implicit request =>

    val namePwd = for {
      formData <- request.body.asFormUrlEncoded
      usernames <- formData.get("username")
      passwords <- formData.get("password")
      username <- usernames.headOption
      password <- passwords.headOption
    } yield (username, password)
    namePwd.map {
      case (username, password) =>
        userService.authenticate(username, password).flatMap {
          case None => Future.successful(Unauthorized("Wrong credentials"))
          case Some(user) =>
            val eventualToken = userService.issueTokenFor(user)
            eventualToken.map(token => Ok(token.tokenValue))
        }
    }.getOrElse(Future.successful(BadRequest("bad-request-01")))

  }
}
