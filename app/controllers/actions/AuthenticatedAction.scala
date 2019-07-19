package controllers.actions


import cats.instances.future._
import cats.syntax.applicative._
import cats.syntax.either._
import javax.inject.Inject
import models.services.UserService
import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @author Dmitry Openkov
  */

class AuthenticatedAction @Inject()(val parser: BodyParsers.Default, val userService: UserService)
                                   (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent]
    with ActionRefiner[Request, UserRequest] {
  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] =
    request.headers.get("X-Auth-Token") match {
      case None => Forbidden("No X-Auth-Token header presented").asLeft.pure[Future]
      case Some(tokenValue) => userService.validateToken(tokenValue).value.map {
        case None => Forbidden("Token not found or expired").asLeft
        case Some(user) => new AuthenticatedRequest(user, request).asRight
      }
    }
}