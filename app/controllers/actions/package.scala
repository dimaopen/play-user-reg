package controllers

import models.Entities.User
import play.api.mvc.Security.AuthenticatedRequest


/**
  *
  * @author Dmitry Openkov
  */
package object actions {
  type UserRequest[A] = AuthenticatedRequest[A, User]

}
