package managers

import scala.util.Try
import exceptions.ResourceException
import resources.CustomerResource

/**
 * @author gmatsu
 *
 *
 */
trait ResourceManager[T,K] {

  def save(doc: T): Try[Option[CustomerResource]]
  def update(doc: T): Either[Option[CustomerResource],Option[ResourceException]]
  def partialUpdate(id: K, doc: T): Either[Option[T],Option[ResourceException]]
  def delete(id: K): Try[Boolean]
  def findById(id: K): Try[Option[T]]

}
