package dao

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

import model.Customer
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import scala.concurrent.Future
import play.modules.reactivemongo.json.BSONFormats._
import com.google.inject.Singleton
import play.api.Logger


/**
 * @author gmatsu
 *
 */
@Singleton
class CustomerDAO extends BaseDAO[Customer,String]{

  override val collectionName = "customers"

  override def save(doc: Customer) = {
    val docToSave = doc.version match {
      case Some(x) => doc
      case None => doc.copy(version = Option(1L))
    }
    collection.insert(docToSave)
  }

  //TODO: change to : fetch.. merge, update, increment version
  override def update(doc: Customer) = collection.update(BSONDocument("id" -> doc.id), doc, upsert = true)

  override def delete(id: String) = collection.remove(BSONDocument("id" -> BSONObjectID(id)))

  override def findById(id: String): Future[Option[Customer]] = collection.find(BSONDocument("id" -> BSONObjectID(id))).cursor[Customer].headOption


  def findDuplicates() = {

  }

  override def findByQuery(params: Map[String, String]): Future[List[Customer]] = {
    //TODO: filter out invalid params.

    Logger.debug("Fi"+params)
    val docs = params.flatMap( pair => {
        Logger.debug(pair._1)
        List(BSONDocument(pair._1 -> pair._2))
      }
    )
    Logger.debug(docs.toString())
//    val results = collection.find(docs)
 //   Logger.debug(""+results)
    //val cursor= .cursor[Customer]
    //cursor.collect[List]()
    null
  }

}
