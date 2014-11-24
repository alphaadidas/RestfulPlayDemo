package managers

import scala.util.Try
import exceptions.ResourceException

/**
 * @author gmatsu
 *
 *
 */
trait ResourceManager[T,K] {

  def save(doc: T)
  def update(id: K, doc: T): Either[Option[T],Option[ResourceException]]
  def delete(id: K)
  def findById(id: K): Try[Option[T]]

}
