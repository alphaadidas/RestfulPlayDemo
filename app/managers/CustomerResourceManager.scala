package managers

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

import dao.CustomerDAO
import com.google.inject.{Inject, Singleton}
import resources.CustomerResource
import exceptions.{ResourceConflictException, ResourceNotFoundException, ResourceException}
import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import mapping.CustomerMapping
import play.api.Logger


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

  def save(doc: CustomerResource) = {
    dao.save(CustomerMapping.fromResource(doc)).map {
      lastError =>
        Logger.debug("new customer saved")
    }
  }

  def update(id:String, doc: CustomerResource): Either[Option[CustomerResource],Option[ResourceException]] = {
    findById(id) match {
      case Success(s) => s match {
        case Some(cust) =>  Left(Option(cust))
        case None => Right(Option(new ResourceNotFoundException))
      }
      case Failure(f) => Right(Option(new ResourceException))
    }
  }

  def delete(id: String) = {
    dao.delete(id).map{
      lastError =>
    }
  }

  def findById(id: String): Try[Option[CustomerResource]] = {
    Try(Await.result(dao.findById(id).flatMap {
      case model@Some(doc) => Future(Option(CustomerMapping.fromModel(model.get)))
      case None => Future(None)
    }, 30 second))
  }

  def findByQuery(query:Map[String,String]) = {
    dao.findByQuery(query)
  }

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
      update(newTarget.get.id,newTarget.get).fold(
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



}

