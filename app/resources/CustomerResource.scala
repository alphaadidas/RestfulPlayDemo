package resources

import play.api.libs.json.Json

/**
 * @author gmatsu
 *
 *
 */
case class CustomerResource(id: String,
                            emailAddress: Option[String] = None,
                            firstName: Option[String] = None,
                            lastName: Option[String] = None,
                            phone: Option[String] = None
                            ) extends BaseResource {
}

//id: Option[BSONObjectID] = Option(BSONObjectID.generate),

object CustomerResource {
  implicit val jsonFormat = Json.format[CustomerResource]
}


case class CustomerResourceList(test:String) extends ResourceListResponse[CustomerResource]{

}

object CustomerResourceList {
  implicit val jsonFormat = Json.format[CustomerResourceList]
}