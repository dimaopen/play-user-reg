package models.dao

import javax.inject.Inject
import models.Entities.{FarmArea, NewFarmer}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 *
 * @author Dmitry Openkov
 */
class DbSetup @Inject()(val dbConfigProvider: DatabaseConfigProvider, val farmerDao: FarmerDao,
                        val farmAreaDao: FarmAreaDao)
                       (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  db.run(farmAreaDao.farmAreas.filter(_.name === "Orange garden").result.headOption).onComplete {
    case Failure(exception) => println("Cannot connect to db: " + exception.getMessage)
    case Success(value) => value match {
      case Some(_) =>
      case None =>
        for {
          option <- db.run(farmAreaDao.farmAreas.filter(_.name === "Orange garden").result.headOption)
        } yield option match {
          case Some(_) =>
          case None =>
            for {
              country <- farmerDao.countryByName("United States")
            } yield for {
              c <- country
            } yield for {
              malId <- farmerDao.insertFarmer(NewFarmer("Mal", "Gibson", "837490227773", country.get.code))
              mikeId <- farmerDao.insertFarmer(NewFarmer("Michael", "Jackson", "23739202371", country.get.code))
              madonnaId <- farmerDao.insertFarmer(NewFarmer("Ginko", "Madonna", "28347290237", country.get.code))
              gloriaId <- farmerDao.insertFarmer(NewFarmer("Gloria", "Geinor", "34458237377", country.get.code))
              neverId <- farmAreaDao.insertFarmArea(FarmArea(0, "Neverland", None, mikeId, c.id))
              _ <- farmAreaDao.insertFarmArea(FarmArea(0, "BBPE", None, malId, c.id))
              hotId <- farmAreaDao.insertFarmArea(FarmArea(0, "Hot Farm", Some(neverId), madonnaId, c.id))
              _ <- farmAreaDao.insertFarmArea(FarmArea(0, "Orange garden", Some(neverId), madonnaId, c.id))
              _ <- farmAreaDao.insertFarmArea(FarmArea(0, "Cherry creek", Some(hotId), gloriaId, c.id))
            } yield ()
        }
    }
  }


}
