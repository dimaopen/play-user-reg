package models.dao

import javax.inject.Inject
import models.Entities.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  *
  * @author Dmitry Openkov
  */
class UserDao @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                       (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "portal_user") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username", O.Unique)

    def hashedPassword = column[Array[Byte]]("hashedpassword")

    def blocked = column[Boolean]("blocked")

    def * = (id, username, hashedPassword, blocked).mapTo[User]
  }

  val users = TableQuery[UserTable]

  def findUser(username: String, hashedPassword: Array[Byte]): Future[Option[User]] = {
    val query = users
      .filter(user => user.username === username && user.hashedPassword === hashedPassword)

    db.run(query.result)
      .map(_.headOption)
  }

  def insertUser(user: User): Future[Int] = {
    val query = (users returning users.map(_.id)) += user
    db.run(query)
  }

}
