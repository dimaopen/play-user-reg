package models.services

import java.time.{Duration, Instant}
import java.util.Base64

import cats.data.OptionT
import cats.instances.future._
import javax.inject.Inject
import models.Entities.{Token, User}
import models.dao.{TokenDao, UserDao}

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @author Dmitry Openkov
  */
class UserService @Inject()(val userDao: UserDao, val tokenDao: TokenDao)
                           (implicit ec: ExecutionContext) {

  userDao.findUser("admin", hash("secret")).foreach {
    case None => userDao.insertUser(User(0, "admin", hash("secret")))
    case _ =>
  }

  def authenticate(username: String, password: String): Future[Option[User]] = {
    userDao.findUser(username, hash(password))
  }

  def issueTokenFor(user: User): Future[Token] = {
    val now = Instant.now()
    val token = Token(generateRandomString(33), now, now.plus(Duration.ofMinutes(15)), user.id)
    tokenDao.insertToken(token)
  }

  def validateToken(tokenValue: String): OptionT[Future, User] = {
      tokenDao.findToken(tokenValue).filter {
        case (token, user) => token.validUntil.isBefore(Instant.now()) && !user.blocked
      }.map {
        case (token, user) => user
      }
  }

  private def hash(str: String): Array[Byte] = {
    import java.nio.charset.StandardCharsets
    import java.security.MessageDigest
    val digest = MessageDigest.getInstance("SHA-256")
    digest.digest(str.getBytes(StandardCharsets.UTF_8))
  }

  private def generateRandomString(length: Int): String = {
    import java.security.SecureRandom
    val r = new SecureRandom()
    val bytes = new Array[Byte](Math.round(length / 4.0f * 3))
    r.nextBytes(bytes)
    val str = Base64.getEncoder.encodeToString(bytes)
    str.substring(0, length)
  }
}
