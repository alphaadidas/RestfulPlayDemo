package mapping

import resources.{CustomerResourceList, CustomerResource}
import model.Customer

import play.api.Logger

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

  def toResourceListFromModel(models: List[Customer]): List[CustomerResource] = {
    models.flatMap( model => List(fromModel(model)))
  }

  def toResourceListFromModel(models: List[Customer], total: Int) = {

  }

  def toResourceListResponse(resources:List[CustomerResource],totalCount:Int) = {
    CustomerResourceList(resources,resources.size)
  }

  def toResourceListResponseFromModel(models: List[Customer]): CustomerResourceList ={
    Logger.debug(s"model count: ${models.size}")
    val resources = toResourceListFromModel(models)
    toResourceListResponse(resources,resources.size)
  }
}
