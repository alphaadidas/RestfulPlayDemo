package model

import play.api.libs.json.Json

/**
 * @author gmatsu
 *
 *
 */
case class Customer(id:String,
                    emailAddress: Option[String],
                    firstName: Option[String],
                    lastName: Option[String],
                    phone: Option[String]) {

}

object Customer {
  implicit val jsonFormat = Json.format[Customer]
}
