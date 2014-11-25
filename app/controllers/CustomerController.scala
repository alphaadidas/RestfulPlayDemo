package controllers

import scala.concurrent.Future
import scala.util.{Failure, Success}

import com.google.inject.{Inject, Singleton}
import com.wordnik.swagger.annotations._
import exceptions.{ResourceNotFoundException, ResourceConflictException, ResourceException}
import javax.ws.rs.PathParam
import managers.CustomerResourceManager
import play.api.Logger
import play.api.mvc.{Action, Controller}
import resources.CustomerResource
import play.api.libs.json.Json

/**
 *
 * Customer API entry point.
 *
 * Thin controller layer, as the business logic is maintained in the Manager Layer and below.
 *
 * @author gmatsu
 *
 */
@Singleton
@Api(value = "/api/v1/customers", description = "Customer Api", basePath = "https://restfulplay.herokuapp.com")
class CustomerController @Inject()(manager: CustomerResourceManager) extends Controller {

  /**
   *
   * @return
   */
  @ApiOperation(
    nickname = "saveNewCustomer",
    value = "Save new Customer",
    notes = "Saves a new Customer",
    httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "New Customer Created"),
    new ApiResponse(code = 400, message = "Invalid Customer Resource Format")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Customer object to be created", required = true, dataType = "CustomerResource", paramType = "body")))
  def save() = Action.async(parse.json) {
    request => {
      Logger.debug("Save New Customer Request")
      request.body.validate[CustomerResource].map {
        customer => {
          manager.save(customer)
          Future.successful(Created)
        }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
    }
  }

  /**
   *
   * @param id - customer Id
   * @return - CustomerResource
   */
  @ApiOperation(
    nickname = "Get Customer By Id",
    value = "Get Customer By Id",
    notes = "Gets a customer by Id",
    response = classOf[resources.CustomerResource],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Gets cutomer by Id", response = classOf[resources.CustomerResource] ),
    new ApiResponse(code = 404, message = "Customer Not found")))
  def get(@ApiParam(value = "Customer Id", required = true, allowMultiple = true) @PathParam("id") id: String) = Action {

    Logger.debug("Getting Customer by id")

    manager.findById(id) match {
      case Success(s) => s match {
        case Some(doc) => Ok(Json.toJson(doc))
        case None => NotFound
      }
      case Failure(f) => {
        Logger.error(s"failure : ${f.getMessage}")
        InternalServerError
      }
    }
  }

  /**
   *
   * @param id
   * @return
   */
  @ApiOperation(
    nickname = "Update Customer with Id",
    value = "Update Customer with Id",
    notes = "Update Customer with Id",
    response = classOf[resources.CustomerResource],
    httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Successfully updated"),
    new ApiResponse(code = 304, message = "Not Modified"),
    new ApiResponse(code = 400, message = "Bad Json Format"),
    new ApiResponse(code = 404, message = "Customer Not found"),
    new ApiResponse(code = 409, message = "Update is in conflict with existing record"),
    new ApiResponse(code = 412, message = "PreCondition failed")
  ))
  def update(id: String) = Action.async(parse.json) {
    request =>
      request.body.validate[CustomerResource].map {
        customer => {
          manager.update(id, customer).fold(
            (success) => {
              Future.successful(Created)
            },
            (error) => {
              error.get match {
                case e: ResourceNotFoundException => Future.successful(NotFound)
                case e: ResourceConflictException => Future.successful(Conflict)
                case e: ResourceException => Future.successful(BadRequest)
              }
            }
          )
        }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  /**
   *
   * @param id
   * @return
   */
  @ApiOperation(
    nickname = "Delete Customer with Id",
    value = "Delete Customer with Id",
    notes = "Delete Customer with Id",
    httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "Successfully Deleted"),
    new ApiResponse(code = 404, message = "Customer Not found"),
    new ApiResponse(code = 412, message = "PreCondition failed")
  ))
  def delete(id: String) = Action {
    manager.delete(id)
    Ok
  }

  /**
   *
   * @return
   */
  @ApiOperation(
    nickname = "Search for Customers",
    value = "Search for Customers",
    notes = "Search for Customers",
    response = classOf[resources.CustomerResourceList],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Customers found", response = classOf[resources.CustomerResourceList]),
    new ApiResponse(code = 404, message = "Customers Not found")
  ))
  def find() = Action {
    request => {
      manager.findByQuery(request.queryString.flatMap( kv => Map(kv._1 -> kv._2.head))) match {
        case Success(s) => s match {
          case Some(result) => Ok(Json.toJson(result))
          case None => NotFound
        }
        case Failure(f) => BadRequest
      }
    }
  }

  /**
   *
   * @return
   */
  @ApiOperation(
    nickname = "Search for Duplicate Customer Records",
    value = "Search for Duplicate Customer Records",
    notes = "Special Query to Search for Duplicate Customer Records",
    response = classOf[resources.CustomerResourceList],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Successfully updated", response = classOf[resources.CustomerResourceList]),
    new ApiResponse(code = 404, message = "Customer Not found")
  ))
  def findDuplicates() = Action {
    request => {
      manager.findByQuery(request.queryString.flatMap( kv => Map(kv._1 -> kv._2.head))) match {
        case Success(s) => s match {
          case Some(result) => Ok(Json.toJson(result))
          case None => NotFound
        }
        case Failure(f) => BadRequest
      }
    }
  }

  /**
   *
   * @param id
   * @return
   */
  @ApiOperation(
    nickname = "Merge Customer Resources",
    value = "Merge Customer Resources",
    notes = "Merge Customer Resources",
    response = classOf[resources.CustomerResource],
    httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Successfully Merged"),
    new ApiResponse(code = 304, message = "Nothing was merged"),
    new ApiResponse(code = 404, message = "Customer Not found")
  ))
  def merge(id: String) = Action {
    request => {
      val ids = request.queryString.get("ids").get.toList
      manager.collapseLeft(id,ids)
      Ok
    }
  }

}

