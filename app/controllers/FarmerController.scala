package controllers

import javax.inject._
import models.Entities.Farmer._
import models.Entities.NewFarmer
import models.dao.FarmerDao
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class FarmerController @Inject()(cc: ControllerComponents, bp: PlayBodyParsers, farmerDao: FarmerDao)
                                (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def farmers() = Action.async { implicit request =>
    farmerDao.allFarmers(0, 10).map(farmers => Ok(Json.toJson(farmers)))
  }

  def newFarmer() = Action.async(bp.json[NewFarmer]) { implicit request =>
    val farmer: NewFarmer = request.body
    farmerDao.insertFarmer(farmer)
      .map(
        id => Created.withHeaders(LOCATION -> (controllers.routes.FarmerController.farmers().path() + '/' + id))
      )
  }
}
