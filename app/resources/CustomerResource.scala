package resources

import play.api.libs.json.Json

/**
 * @author gmatsu
 *
 *
 */
case class CustomerResource(id: Option[String] = None,
                            emailAddress: Option[String] = None,
                            firstName: Option[String] = None,
                            lastName: Option[String] = None,
                            phone: Option[String] = None
                            ) extends BaseResource {
}

object CustomerResource {
  implicit val jsonFormat = Json.format[CustomerResource]
}


case class CustomerResourceList(override  val results: List[CustomerResource],
                                 override val resultCount: Int,
                                 override val hasMore: Boolean = false,
                                 override val nextPageToken: Option[String] = None
                                 ) extends ResourceListResponse[CustomerResource]{
  override def resourceName = "CustomerResource"
  //TODO: add support for "Token-Based Result Cursor Paging"

}

object CustomerResourceList {
  implicit val jsonFormat = Json.format[CustomerResourceList]
}


case class DuplicateCustomerResource(emailAddress: String, duplicates : List[CustomerResource]) extends BaseResource
//, _links: Hyp )

object DuplicateCustomerResource {
  implicit val jsonFormat = Json.format[DuplicateCustomerResource]
}

case class DuplicateCustomerResourceList(override  val results: List[DuplicateCustomerResource],
                                         override val resultCount: Int,
                                         override val hasMore: Boolean = false,
                                         override val nextPageToken: Option[String] = None
                                         ) extends ResourceListResponse[DuplicateCustomerResource]{

  override def resourceName: String = "DuplicateCustomer"
}

object DuplicateCustomerResourceList {
  implicit val jsonFormat = Json.format[DuplicateCustomerResourceList]
}
