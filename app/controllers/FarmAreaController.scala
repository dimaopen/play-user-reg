package controllers

import controllers.actions.AuthenticatedAction
import javax.inject._
import models.Entities.FarmArea
import models.dao.FarmAreaDao
import org.slf4j.LoggerFactory
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class FarmAreaController @Inject()(cc: ControllerComponents, bp: PlayBodyParsers, farmAreaDao: FarmAreaDao
                                   , authAction: AuthenticatedAction)
                                  (implicit ec: ExecutionContext) extends AbstractController(cc) {
  val logger = LoggerFactory.getLogger("FarmAreaController")

  implicit val simpleWrites: Writes[FarmArea] = new Writes[FarmArea] {
    def writes(f: FarmArea) = Json.obj(
      "id" -> f.id,
      "name" -> f.name,
    )
  }

  def farmAreas(parentId: Option[Int], offset: Option[Int], limit: Option[Int]) = Action.async { implicit request =>
    farmAreaDao.farmAreasByParent(parentId, offset.getOrElse(0), limit.getOrElse(10)).map(farmAreas => Ok(Json.toJson(farmAreas)))
  }

  def superiors(areaId: Int) = Action.async { implicit request =>
    farmAreaDao.superiors(areaId).map(farmAreas => Ok(Json.toJson(farmAreas)))
  }

 /* def newFarmArea() = authAction.async(bp.json[NewFarmArea]) { implicit request =>
    val farmArea: NewFarmArea = request.body
    logger.info("User {} inserting new farmArea named '{} {}'", request.user.username, farmArea.firstName, farmArea.lastName)
    farmAreaDao.insertFarmArea(farmArea)
      .map(
        id => Created.withHeaders(LOCATION -> (controllers.routes.FarmAreaController.farmAreas().path() + '/' + id))
      )
  }*/
}
