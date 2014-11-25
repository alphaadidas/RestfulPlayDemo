package model

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.Macros.Annotations.Key

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
