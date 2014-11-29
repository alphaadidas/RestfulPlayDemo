package model

import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.Macros.Annotations.Key
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._

/**
 * @author gmatsu
 *
 *
 */
case class Customer(@Key("_id") id: Option[BSONObjectID] = Option(BSONObjectID.generate),
                    emailAddress: Option[String] = None,
                    firstName: Option[String] = None,
                    lastName: Option[String] = None,
                    phone: Option[String] = None,
                    version: Option[Long]) {
}

object Customer {
  implicit val jsonFormat = Json.format[Customer]

}


case class DuplicateEmailCount(emailAddress: String , total : Int){}

object DuplicateEmailCount {


  implicit val duplicateEmailCountRead: Reads[DuplicateEmailCount] = (
    (__ \ "_id").read[String]
      and (__ \ "total").read[Int]
    )(DuplicateEmailCount.apply _)

  implicit val jsonFormat = Json.format[DuplicateEmailCount]
}
//list duplicatesby email?

//Duplicates
//