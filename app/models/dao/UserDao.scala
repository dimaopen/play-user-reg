package models.dao

import javax.inject.Inject
import models.Entities
import models.Entities.User
import models.dao.UserDao.UserRow
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  *
  * @author Dmitry Openkov
  */
class UserDao @Inject()(val dbConfigProvider: DatabaseConfigProvider, val personDao: PersonDao)
                       (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  class UserTable(tag: Tag) extends Table[UserRow](tag, "portal_user") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username", O.Unique)

    def hashedPassword = column[Array[Byte]]("hashedpassword")

    def blocked = column[Boolean]("blocked")

    def personId = column[Int]("person_id")

    def * = (id, username, hashedPassword, blocked, personId).mapTo[UserRow]

    def person = foreignKey("fk_portal_user_person_id", personId, personDao.persons)(_.id)
  }

  val users = TableQuery[UserTable]

  def findUser(username: String, hashedPassword: Array[Byte]): Future[Option[User]] = {
    import personDao.PersonTable
    val query = (users join personDao.persons on ((u: UserTable, p: PersonTable) => u.personId === p.id))
      .filter{
        case (user, person) => user.username === username && user.hashedPassword === hashedPassword
      }

    for {
      res <- db.run(query.result)
    } yield {
      for {
        (user, person) <- res.headOption
      } yield createUser(user, person)
    }
  }

  def createUser(user: UserRow, person: Entities.Person) =
    User(user.id, user.username, user.hashedPassword, user.blocked, person)

  def insertUser(user: User): Future[Int] = {
    val query = (users returning users.map(_.id)) +=
      UserRow(user.id, user.username, user.hashedPassword, user.blocked, user.person.id)
    db.run(query)
  }

}

object UserDao {
  case class UserRow(id: Int, username: String, hashedPassword: Array[Byte], blocked: Boolean, personId: Int)
}
