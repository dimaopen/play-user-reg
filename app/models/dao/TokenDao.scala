package models.dao

import java.sql.Timestamp
import java.time.Instant

import cats.data.OptionT
import cats.instances.future._
import javax.inject.Inject
import models.Entities.{Person, Token, User}
import models.dao.UserDao.UserRow
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  *
  * @author Dmitry Openkov
  */
class TokenDao @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userDao: UserDao)
                        (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  class TokenTable(tag: Tag) extends Table[Token](tag, "token") {

    implicit val prof = dbConfig.profile
    import models.dao.CustomColumnTypes.timeInstantType

    def tokenValue = column[String]("token_value", O.PrimaryKey)

    def issued = column[Instant]("issued")

    def validUntil = column[Instant]("valid_until")

    def userId = column[Int]("user_id")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (tokenValue, issued, validUntil, userId).mapTo[Token]

    def user = foreignKey("fk_token_user_id", userId, userDao.users)(_.id)
  }

  val tokens = TableQuery[TokenTable]

  def insertToken(token: Token): Future[Token] = {
    val query = (tokens returning tokens) += token
    db.run(query)
  }

  def findToken(tokenValue: String): OptionT[Future, (Token, User)] = {
    val query = for {
      token <- tokens.filter(_.tokenValue === tokenValue)
      user <- token.user
      person <- user.person
    } yield (token, user, person)
    val eventualMaybeTuple: Future[Option[(Token, UserRow, Person)]] = db.run(query.result).map(_.headOption)
    for {
      (token, user, person) <- OptionT(eventualMaybeTuple)
    } yield (token, userDao.createUser(user, person))
  }

}
