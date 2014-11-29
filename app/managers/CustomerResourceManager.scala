package managers

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

import ExecutionContext.Implicits.global
import com.google.inject.{Inject, Singleton}
import dao.CustomerDAO
import exceptions.{ResourceConflictException, ResourceException, ResourceNotFoundException}
import mapping.CustomerMapping
import model.DuplicateEmailCount
import play.api.Logger
import resources.{DuplicateCustomerResourceList, DuplicateCustomerResource, CustomerResource, CustomerResourceList}
import scala.collection.mutable

/**
 * Layer to Manage/Orchestrate the logic/business-process around a Rest Resource.
 * This version will just transfer/validate the resource and save to a model.
 * Simple Merge logic here too.
 *
 * @author gmatsu
 *
 */
@Singleton
class CustomerResourceManager @Inject()(customerDao: CustomerDAO) extends ResourceManager[CustomerResource,String] {

  val dao = customerDao

  val DAO_TIMEOUT = 30.second

  /**
   *
   * @param doc
   */
  def save(doc: CustomerResource): Try[Option[CustomerResource]] = {
    val customer = CustomerMapping.fromResource(doc)
    val id = customer.id
    Await.result(
      dao.save(customer).map {
        lastError =>
          Logger.debug("new customer saved")
          if(!lastError.ok){
            throw new ResourceException
          } else {
            findById(id.get.stringify)
          }
      }, DAO_TIMEOUT)
  }

  /**
   *
   * @param id
   * @param doc
   * @return
   */
  def partialUpdate(id:String, doc: CustomerResource): Either[Option[CustomerResource],Option[ResourceException]] = {

    findById(id) match {
      case Success(s) => s match {
        case Some(cust) =>  Left(Option(cust))
        case None => Right(Option(new ResourceNotFoundException))
      }
      case Failure(f) => Right(Option(new ResourceException))
    }
  }

  /**
   *
   * @param doc
   * @return
   */
  def update(doc: CustomerResource): Either[Option[CustomerResource],Option[ResourceException]] = {

    findById(doc.id.get) match {
      case Success(s) => s match {
        case Some(cust) =>  Left(Option(cust))
        case None => Right(Option(new ResourceNotFoundException))
      }
      case Failure(f) => Right(Option(new ResourceException))
    }
  }

  /**
   *
   * @param id
   */
  def delete(id: String): Try[Boolean] = {
    Try(
      Await.result(dao.delete(id).map {
        lastError =>
          if(lastError.ok && lastError.updatedExisting) true
          else if (lastError.ok && !lastError.updatedExisting) throw new ResourceNotFoundException
          else false
      },DAO_TIMEOUT))
  }

  /**
   *
   * @param id
   * @return
   */
  def findById(id: String): Try[Option[CustomerResource]] = {
    Try(Await.result(dao.findById(id).flatMap {
      case model@Some(doc) => Future(Option(CustomerMapping.fromModel(model.get)))
      case None => Future(None)
    }, DAO_TIMEOUT))
  }

  /**
   *
   * @param query
   * @return
   */
  def findByQuery(query:Map[String,String]): Try[Option[CustomerResourceList]] = {
    Try(
      Await.result(
        dao.findByQuery(query).flatMap{
          case results => Future(Option(CustomerMapping.toResourceListResponseFromModel(results)))
          case _ => Future(None)
        }, DAO_TIMEOUT)
    )
  }

  def findDuplicates():Future[DuplicateCustomerResourceList] = {
    //TODO: fix this for scala async style.

    val duplicates = Await.result(dao.findDuplicates(),DAO_TIMEOUT)

    var duplicateResource: mutable.MutableList[DuplicateCustomerResource] =  mutable.MutableList()

    duplicates.foreach ( dup => {
      val models = Await.result(dao.findByQuery(Map("emailAddress" -> dup.emailAddress)),DAO_TIMEOUT)
      val resource = CustomerMapping.toResourceListFromModel(models)
      duplicateResource+= DuplicateCustomerResource(dup.emailAddress, resource)
    })

    Future(DuplicateCustomerResourceList(duplicateResource.toList,duplicates.size))
  }


  /**
   *
   * @param targetId
   * @param sourceId
   * @return
   */
  def mergeLeft(targetId: String, sourceId: String): Either[Option[CustomerResource],Option[ResourceException]] = {
    if(targetId == sourceId) Right(Option(new ResourceConflictException))

    var newTarget: Option[CustomerResource] = None

    for(
      targetObj <- findById(targetId);
      sourceObj <- findById(sourceId)
    ) yield {
      val t = targetObj.get
      val s = sourceObj.get
      newTarget =  Option(t.copy( id = t.id,
        emailAddress = if(t.emailAddress.isEmpty && s.emailAddress.isDefined) s.emailAddress else t.emailAddress,
        firstName = if(t.firstName.isEmpty && s.firstName.isDefined) s.firstName else t.firstName,
        lastName = if(t.lastName.isEmpty && s.lastName.isDefined) s.lastName else t.lastName,
        phone = if(t.phone.isEmpty && s.phone.isDefined) s.phone else t.phone
      ))
    }

    if(newTarget.isDefined){
      update(newTarget.get).fold(
        (success) => {
          Left(success)
        },
        (error) =>{
          Right(Option(new ResourceException))
        }
      )
    }
    else{
      Right(Option(new ResourceException))
    }

  }

  /**
   *
   * @param targetId
   * @param sourceIds
   */
  def collapseLeft(targetId: String, sourceIds: List[String]) = {


    //
    // if rights ..emtpy return left
    // return collapes(head, tail)
    //
    //


  }



}

