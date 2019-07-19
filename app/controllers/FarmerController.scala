package controllers

import controllers.actions.AuthenticatedAction
import javax.inject._
import models.Entities.Farmer._
import models.Entities.NewFarmer
import models.dao.FarmerDao
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class FarmerController @Inject()(cc: ControllerComponents, bp: PlayBodyParsers, farmerDao: FarmerDao
                                 , authAction: AuthenticatedAction)
                                (implicit ec: ExecutionContext) extends AbstractController(cc) {
  val logger = LoggerFactory.getLogger("FarmerController")

  def farmers() = Action.async { implicit request =>
    farmerDao.allFarmers(0, 10).map(farmers => Ok(Json.toJson(farmers)))
  }

  def newFarmer() = authAction.async(bp.json[NewFarmer]) { implicit request =>
    val farmer: NewFarmer = request.body
    logger.info("User {} inserting new farmer named '{} {}'", request.user.username, farmer.firstName, farmer.lastName)
    farmerDao.insertFarmer(farmer)
      .map(
        id => Created.withHeaders(LOCATION -> (controllers.routes.FarmerController.farmers().path() + '/' + id))
      )
  }
}
