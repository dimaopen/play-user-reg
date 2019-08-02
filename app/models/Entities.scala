package models

import java.time.{Instant, LocalDate}

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
  *
  * @author Dmitry Openkov
  */
object Entities {

  case class Country(id: Int, code: String, name: String)

  case class Farmer(id: Int, firstName: String, lastName: String, taxNumber: String, countryCode: String)

  case class NewFarmer(firstName: String, lastName: String, taxNumber: String, countryCode: String)

  object Farmer extends Function5[Int, String, String, String, String, Farmer] {
    implicit val defaultWrites: Writes[Farmer] = new Writes[Farmer] {
      def writes(f: Farmer) = Json.obj(
      "id" -> f.id,
      "firstName" -> f.firstName,
      "lastName" -> f.lastName,
      "countryCode" -> f.countryCode,
      )
    }

    implicit val defaultReads: Reads[NewFarmer] = (
                     (JsPath \ "firstName").read[String](minLength[String](2) keepAnd maxLength[String](100)) and
                     (JsPath \ "lastName").read[String](minLength[String](2) keepAnd maxLength[String](100)) and
                     (JsPath \ "taxNumber").read[String](minLength[String](4) keepAnd maxLength[String](40)) and
                     (JsPath \ "countryCode").read[String](maxLength[String](2))
    ) (NewFarmer.apply _)

  }

  case class FarmArea(id: Int, name: String, parentId: Option[Int], farmerId: Int, countryId: Int)

  case class Role(id: Int, name: String)

  object Permission extends Enumeration {
    type Permission = Value
    val HiringWorkers = Value("Hiring")
    val PaperWork = Value("Papers")
    val FarmerManager = Value("Manager")
  }

  case class Person(id: Int, firstName: String, lastName: String, birthDay: LocalDate, originId: Int)
  case class Employee(id: Int, person: Person, farmAreaId: Int)

  case class User(id: Int, username: String, hashedPassword: Array[Byte], blocked: Boolean = false, person: Person)

  case class Token(tokenValue: String, issued: Instant, validUntil: Instant, userId: Int)

}
