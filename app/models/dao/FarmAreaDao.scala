package models.dao

import javax.inject.Inject
import models.Entities.FarmArea
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{GetResult, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}


/**
  *
  * @author Dmitry Openkov
  */
class FarmAreaDao @Inject()(val dbConfigProvider: DatabaseConfigProvider, val farmerDao: FarmerDao)
                           (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
  import dbConfig.profile.api._

  class FarmerTable(tag: Tag) extends Table[FarmArea](tag, "farm_area") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def parentId = column[Option[Int]]("parent_id")
    def farmerId = column[Int]("farmer_id")
    def countryId = column[Int]("country_id")
    def * = (id, name, parentId, farmerId, countryId).mapTo[FarmArea]
    def parent = foreignKey("farm_area_parent_id_fkey", parentId, farmAreas)(_.id)
    def farmer = foreignKey("farm_area_farmer_id_fkey", farmerId, farmerDao.farmers)(_.id)
    def country = foreignKey("farm_area_country_id_fkey", countryId, farmerDao.countries)(_.id)
  }

  val farmAreas = TableQuery[FarmerTable]

  def farmAreasByParent(parentId: Option[Int], offset: Int, limit: Int): Future[Seq[FarmArea]] = {
    val query = parentId.map(parentId => farmAreas.filter(_.parentId === parentId)).getOrElse(farmAreas)
    db.run(query.sortBy(_.name).drop(offset).take(limit).result)
  }

  def farmAreasByName(name: String): Future[Option[FarmArea]] = {
    val query = farmAreas.filter(_.name === name)
    db.run(query.result.headOption)
  }

  implicit val getFarmArea = GetResult(r =>
    FarmArea(
      id = r.<<,
      name = r.<<,
      parentId = r.<<,
      farmerId = r.<<,
      countryId = r.<<,
    )
  )
  
  def superiors(areaId: Int): Future[Seq[FarmArea]] = {
    val action: DBIO[Seq[FarmArea]] =
      sql"""WITH RECURSIVE r(num, id, parent_id) AS (
        SELECT 0, id, parent_id FROM farm_area WHERE id = $areaId
        UNION
        SELECT num + 1, farm_area.id, farm_area.parent_id FROM farm_area JOIN r ON r.parent_id = farm_area.id
     )
     SELECT farm_area.* FROM r INNER JOIN farm_area ON r.id = farm_area.id
     ORDER BY r.num
        """.as[FarmArea]
    db.run(action)
  }

  def insertFarmArea(farmArea: FarmArea): Future[Int] = {
    val query = (farmAreas returning farmAreas.map(_.id)) += farmArea
    db.run(query)
  }

}
