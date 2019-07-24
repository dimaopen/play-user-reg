package models.dao

import java.time.LocalDate

import javax.inject.Inject
import models.Entities.Person
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  *
  * @author Dmitry Openkov
  */
class PersonDao @Inject()(val dbConfigProvider: DatabaseConfigProvider, val farmerDao: FarmerDao)
                         (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

  import dbConfig.profile.api._
  import models.dao.CustomColumnTypes.localDateType

  class PersonTable(tag: Tag) extends Table[Person](tag, "person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def birthDay = column[LocalDate]("birth_day")
    def originId = column[Int]("origin_id")
    def * = (id, firstName, lastName, birthDay, originId).mapTo[Person]
    def country = foreignKey("fk_person_country_id", originId, farmerDao.countries)(_.id)
  }

  val persons: TableQuery[PersonTable] = TableQuery[PersonTable]

  def allPersons(offset: Int, limit: Int): Future[Seq[Person]] = {
    db.run(persons.result)
  }

}
