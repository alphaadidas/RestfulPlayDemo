package dao

import scala.Some
import scala.concurrent.{ExecutionContext, Future}

import ExecutionContext.Implicits.global
import com.google.inject.Singleton
import model.{Customer, DuplicateEmailCount}
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson._
import reactivemongo.core.commands.{Aggregate, GroupField, Match, SumValue}

/**
 * @author gmatsu
 *
 */
@Singleton
class CustomerDAO extends BaseDAO[Customer,String]{

  override val collectionName = "customers"
  val MAX_RESULT_SIZE = 100

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

  override def findById(id: String): Future[Option[Customer]] = collection.find(BSONDocument("id" -> BSONObjectID(id))).one[Customer]

  /**
   * Use aggregation in mongo to find duplicate emailAddress customers.
   * @return
   */
  def findDuplicates(): Future[List[DuplicateEmailCount]] = {
    val command = Aggregate(collection.name, Seq(
      GroupField("emailAddress")("total" -> SumValue(1)),
      Match(BSONDocument("total" -> BSONDocument("$gt" -> 1)))
    ))

    collection.db.command(command) map { result =>
      result.toList map (Json.toJson(_).as[DuplicateEmailCount](DuplicateEmailCount.duplicateEmailCountRead))
    }
  }

  override def findByQuery(params: Map[String, String]): Future[List[Customer]] = {
    //TODO: filter out invalid params.
    Logger.debug(s"Find by Query with params : ${params.toString()}")

    val docs = params.flatMap( pair => {
        Logger.debug(pair._1)
        List(pair._1 -> BSONString(pair._2))
      }
    )
    val results = collection.find(BSONDocument(docs.toTraversable))
    val cursor= results.cursor[Customer]
    cursor.collect[List](MAX_RESULT_SIZE)
  }

}
