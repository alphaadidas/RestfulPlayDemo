package controllers

import scala.Some
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import ExecutionContext.Implicits.global

import com.google.inject.{Inject, Singleton}
import com.wordnik.swagger.annotations._
import exceptions._
import javax.ws.rs.PathParam
import managers.CustomerResourceManager
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import resources.CustomerResource

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
@Api(value = "/api/v1/customers", description = "Customer Api")
class CustomerController @Inject()(manager: CustomerResourceManager) extends Controller {

  /**
   *
   * @return
   */
  @ApiOperation(
    nickname = "saveNewCustomer",
    value = "Save new Customer",
    notes = "Saves a new Customer",
    response = classOf[resources.CustomerResource],
    httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "New Customer Created",response = classOf[resources.CustomerResource]),
    new ApiResponse(code = 400, message = "Invalid Customer Resource Format")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Customer object to be created", required = true, dataType = "CustomerResource", paramType = "body")))
  def save() = Action.async(parse.json) {
    request => {
      Logger.debug(s"Save New Customer Request")
      request.body.validate[CustomerResource].map {
        customer => {
          manager.save(customer) match {
            case Success(s) => Future.successful(Created(Json.toJson(s.get)))
            case Failure(f) => f match {
              case e: ResourceNotModifiedException => Future.successful(NotModified)
              case e: ResourceException => Future.successful(BadRequest)
              case _ => Future.successful(BadRequest)
            }
          }
        }
      }.getOrElse(Future.successful(BadRequest("{}").withHeaders(CONTENT_TYPE -> JSON)))
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
  def get(@ApiParam(value = "Customer Id", required = true, allowMultiple = false) @PathParam("id") id: String) = Action {
    manager.findById(id) match {
      case Success(s) => s match {
        case Some(doc) => Ok(Json.toJson(doc))
        case None => NotFound
      }
      case Failure(f) => {
        f match {
          case e :IllegalArgumentException => NotFound
          case _ => {
            Logger.error(s"failure : ${f.getMessage}")
            InternalServerError
          }
        }
      }
    }
  }


  /**
   *
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
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Customer object to be updated completely", required = true, dataType = "CustomerResource", paramType = "body")))
  def update() = Action.async(parse.json) {
    request =>
      request.body.validate[CustomerResource].map {
        customer => {
          manager.update(customer).fold(
            (success) => {
              Future.successful(Ok)
            },
            (error) => {
              error.get match {
                case e: ResourcePreconditionException => Future.successful(PreconditionFailed)
                case e: ResourceNotModifiedException=> Future.successful(NotModified)
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
    nickname = "Partially Update Customer with Id",
    value = "Partially Update Customer with Id",
    notes = "Partially Update Customer with Id",
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
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Customer object to be partially updated", required = true, dataType = "CustomerResource", paramType = "body")))
  def partialUpdate(@ApiParam(value = "Customer Id", required = true) @PathParam("id") id: String) = Action.async(parse.json) {
    request =>
      request.body.validate[CustomerResource].map {
        customer => {
          manager.partialUpdate(id, customer).fold(
            (success) => {
              Future.successful(Ok)
            },
            (error) => {
              error.get match {
                case e: ResourcePreconditionException => Future.successful(PreconditionFailed)
                case e: ResourceNotModifiedException=> Future.successful(NotModified)
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
    httpMethod = "DELETE",
    response = classOf[Void]
  )
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "Successfully Deleted"),
    new ApiResponse(code = 404, message = "Customer Not found"),
    new ApiResponse(code = 412, message = "PreCondition failed")
  ))
  def delete(@ApiParam(value = "Customer Id", required = true) @PathParam("id") id: String) = Action.async(parse.empty) {
    request => {
      Logger.debug("Delete..")
      manager.delete(id) match {
        case Success(s) => if (s) Future.successful(Ok) else Future.successful(BadRequest)
        case Failure(f) => f match {
          case e: ResourceNotFoundException => Future.successful(NotFound)
          case _ => Future.successful(BadRequest)
        }
      }
    }
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
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "emailAddress", required = false, dataType = "emailAddress", paramType = "query")
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
    response = classOf[resources.DuplicateCustomerResourceList],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Successfully Found Duplicates", response = classOf[resources.DuplicateCustomerResourceList]),
    new ApiResponse(code = 404, message = "No Customer Duplicates found")
  ))
  def findDuplicates() = Action.async {
    manager.findDuplicates() match {
      case Success(s) => s match {
        case Some(result) => Future.successful(Ok(Json.toJson(s.get)))
      }
      case Failure(f) => Future.successful(BadRequest)
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
    notes = "Merge Customer Resources, The list of Id's will be merged/collapsed into the target Id",
    response = classOf[resources.CustomerResource],
    httpMethod = "PUT")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Successfully Merged"),
    new ApiResponse(code = 304, message = "Nothing was merged"),
    new ApiResponse(code = 404, message = "Customer Not found")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "ids", required = false, dataType = "ids", paramType = "query")
  ))
  def merge(@ApiParam(value = "Target Customer Id", required = true, allowMultiple = false) @PathParam("id") id: String) = Action.async(parse.empty) {
    request => {

      val ids = request.queryString.get("ids").get.head.split(",").map( _.trim).toList
      manager.collapseLeft(id,ids) map { results =>
        Ok
      }
    }
  }


}

