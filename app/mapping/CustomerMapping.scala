package mapping

import resources.{CustomerResourceList, CustomerResource}
import model.Customer
import reactivemongo.bson.BSONObjectID

/**
 * @author gmatsu
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
    CustomerResource(Option(model.id.get.stringify),
      emailAddress = model.emailAddress,
      firstName = model.firstName,
      lastName = model.lastName,
      phone = model.phone
    )
  }


  def toResourceListFromModel(models: List[Customer]): CustomerResourceList = {

    CustomerResourceList("hi")
  }

  def toResourceListFromModel(models: List[Customer], total: Int) = {

  }
}
