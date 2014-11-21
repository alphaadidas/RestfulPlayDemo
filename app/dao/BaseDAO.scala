package dao

/**
 * @author gmatsu
 *
 *
 */
trait BaseDAO[T,K] {

//  def dao

  def save(doc:T)

  def update(doc:T)

  def delete(id: K)

}
