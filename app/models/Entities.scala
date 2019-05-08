package models

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

  object Farmer {
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

}
