package mapping

import resources.CustomerResource
import model.Customer
import reactivemongo.bson.BSONObjectID

/**
 * @author: gmatsu
 *
 *
 */
object CustomerMapping {


  def fromResource(resource : CustomerResource): Customer = {
    Customer( emailAddress = resource.emailAddress,
      firstName =  resource.firstName,
      lastName = resource.lastName,
      phone = resource.phone,
      version = Option(1L))
  }

  def fromModel(model : Customer): CustomerResource = {
    CustomerResource(model.id.toString)
  }


}
