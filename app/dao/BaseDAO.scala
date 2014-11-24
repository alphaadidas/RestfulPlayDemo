package dao


import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError
import scala.concurrent.Future

/**
 * Basic CRUD DAO.
 *
 * @author gmatsu
 *
 *
 */
trait BaseDAO[T,K] {

  val collectionName = ""

  def db = ReactiveMongoPlugin.db

  def collection: JSONCollection = db.collection[JSONCollection](collectionName)

  def save(model: T): Future[LastError]

  def update(doc: T): Future[LastError]

  def delete(id: K): Future[LastError]

  def findById(id: K): Future[Option[T]]

  def findByQuery(params: Map[String,String]): Future[List[T]]

}
