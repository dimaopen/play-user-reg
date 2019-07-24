package models.dao

import javax.inject.Inject
import models.Entities.{Country, Farmer, NewFarmer}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}



/**
  *
  * @author Dmitry Openkov
  */
class FarmerDao @Inject() (val dbConfigProvider: DatabaseConfigProvider)
                          (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

  import dbConfig.profile.api._

  class CountryTable(tag: Tag) extends Table[Country](tag, "country") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def code = column[String]("code")
    def name = column[String]("name", O.Unique)
    def * = (id, code, name).mapTo[Country]
  }

  val countries = TableQuery[CountryTable]

  case class FarmerRow(id: Option[Int], firstName: String, lastName: String, taxNumber: String, countryId: Int)

  class FarmerTable(tag: Tag) extends Table[FarmerRow](tag, "farmer") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def taxNumber = column[String]("tax_number")
    def countryId = column[Int]("country_id")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id.?, firstName, lastName, taxNumber, countryId).mapTo[FarmerRow]
    def country = foreignKey("fk_farmer_country_id", countryId, countries)(_.id)
  }

  val farmers = TableQuery[FarmerTable]

  def allFarmers(offset: Int, limit: Int): Future[Seq[Farmer]] = {
    val query = for {
      f <- farmers
      c <- f.country
    } yield (f.id, f.firstName, f.lastName, f.taxNumber, c.code)
    db.run(query.result).map(_.map(Farmer.tupled))
  }

  def insertFarmer(nf: NewFarmer): Future[Int] = {
    val query = (farmers returning farmers.map(_.id)) += FarmerRow(None, nf.firstName, nf.lastName, nf.taxNumber, 13)
    db.run(query)
  }

}
