package models.dao

import java.io.File

import models.Entities
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Environment, Mode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.inject.bind

import scala.util.{Failure, Success}

/**
 *
 * @author Dmitry Openkov
 */
class FarmAreaDaoSpec extends PlaySpec {
  private val pathToApp = new File(".")
  println(s"pathToApp.getAbsolutePath = ${pathToApp.getAbsolutePath}")
  val application: Application = new GuiceApplicationBuilder()
    .in(Environment(pathToApp, this.getClass.getClassLoader, Mode.Test))
    .configure(Configuration("slick.dbs.default.db.url" -> "jdbc:postgresql://localhost:5432/farmers_test"))
    .bindings(bind[DbSetup].toSelf.eagerly())
    .build()
  val daoT: FarmAreaDao = application.injector.instanceOf[FarmAreaDao]

  "FarmAreaDao" should {

    "return all superiors of an area" in {
      val futureAreas = for {
        cherryO <- daoT.farmAreasByName("Cherry creek")
        areas <- daoT.superiors(cherryO.get.id)
      } yield areas

      val areas: Seq[Entities.FarmArea] = Await.result(futureAreas, 5 seconds)
      areas.length mustBe 3
      areas(0).name mustBe "Cherry creek"
      areas(1).name mustBe "Hot Farm"
      areas(2).name mustBe "Neverland"

    }
  }
}
